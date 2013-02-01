/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.los.crud;

import java.util.List;
import java.util.Date;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.BasicEntity;
import org.mywms.model.User;
import org.mywms.model.WorkVehicle;
import org.mywms.model.WorkVehicleHistory;
import org.mywms.model.WorkItem;
import org.mywms.model.WorkItemHistory;
import org.mywms.service.BasicService;
import org.mywms.service.UserService;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.BusinessObjectHelper;
import de.linogistix.los.util.GenericTypeResolver;

/**
 * CRUD (basic) operations for BasicEntities.
 *
 * C: create <br>
 * R: retrieve (handled in super class) <br>
 * U: update <br>
 * D: delete
 *
 * @author trautm
 *
 * @param <T>
 */
@RolesAllowed( { org.mywms.globals.Role.ADMIN_STR,
                 org.mywms.globals.Role.OPERATOR_STR,
                 org.mywms.globals.Role.FOREMAN_STR,
                 org.mywms.globals.Role.INVENTORY_STR,
                 org.mywms.globals.Role.CLEARING_STR
               })
public abstract class BusinessObjectCRUDBean<T extends BasicEntity> implements
    BusinessObjectCRUDRemote<T> {

    private static Logger log = Logger.getLogger(BusinessObjectCRUDBean.class);

    /**
     * Implement to return the correct subclass of BasicService
     *
     * @return subclass of BasicService to use
     */
    protected abstract BasicService<T> getBasicService();

    @Resource
    SessionContext ctx;

    @EJB
    UserService userService;

    @Resource
    EJBContext context;

    @PersistenceContext(unitName = "myWMS")
    protected EntityManager manager;

    private Class<BasicEntity> tClass;

    @SuppressWarnings("unchecked")
    public BusinessObjectCRUDBean() {
        tClass = (Class<BasicEntity>) new GenericTypeResolver<T>().resolveGenericType(getClass());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.linogistix.los.crud.BusinessObjectCRUDRemote#create()
     */
    public T create() throws BusinessObjectExistsException,
        BusinessObjectCreationException, BusinessObjectSecurityException {
        try {
            T entity = (T) getBasicService().getEntityClass().newInstance();
            if (entity instanceof BasicClientAssignedEntity) {
                BasicClientAssignedEntity clientEntity = (BasicClientAssignedEntity) entity;
                User user = getCallersUser();
                if (!user.hasRole(org.mywms.globals.Role.ADMIN_STR)) {
                    if (!clientEntity.getClient().equals(user.getClient())) {
                        log.error("Wrong userClient " + user.getClient()
                                  + " for entityClient "
                                  + clientEntity.getClient());
                        throw new BusinessObjectSecurityException(user);
                    }
                }
            }
            manager.persist(entity);
            return entity;
        } catch (Throwable t) {
            throw new BusinessObjectCreationException();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.linogistix.los.crud.BusinessObjectCRUDRemote#create(T)
     */
    @SuppressWarnings("unchecked")
    public T create(T entity) throws BusinessObjectExistsException,
        BusinessObjectCreationException, BusinessObjectSecurityException {

        T newE;

        if (entity == null) {
            log.error("entity must not be null");
            throw new BusinessObjectCreationException();
        }

        if (!new BusinessObjectHelper(this.ctx, this.userService, this.context)
                .checkClient(entity)) {
            throw new BusinessObjectSecurityException(new BusinessObjectHelper(
                        this.ctx, this.userService, this.context).getCallersUser());
        }

        try {
            newE = (T) entity.getClass().newInstance();
            mergeInto(entity, newE);
            manager.persist(newE);
            // log.debug("result of merge: " + newE.toString());
            // manager.flush();
            User user = getCallersUser();
            log
            .info(user.getName() + " created: "
                  + newE.toDescriptiveString());
            return newE;

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new BusinessObjectCreationException();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.linogistix.los.crud.BusinessObjectCRUDRemote#delete(T)
     */
    public void delete(T entity) throws BusinessObjectNotFoundException,
        BusinessObjectDeleteException, BusinessObjectSecurityException {

        if (entity == null) {
            log.error("entity must not be null");
            throw new BusinessObjectDeleteException();
        }

        if (!checkClient(entity)) {
            throw new BusinessObjectSecurityException(getCallersUser());
        }

        try {
            entity = (T) manager.find(getBasicService().getEntityClass(),
                                      entity.getId());
            User user = getCallersUser();
            log.info(user.getName() + " deleted: "
                     + entity.toDescriptiveString());
            manager.remove(entity);
        } catch (IllegalArgumentException iaex) {
            log.error(iaex.getMessage(), iaex);
            throw new BusinessObjectNotFoundException();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new BusinessObjectDeleteException();
        }
    }

    /*
     * (non-Javadoc)
     * @see de.linogistix.los.crud.BusinessObjectCRUDRemote#delete(java.util.List)
     */
    public void delete(List<BODTO<T>> list) throws BusinessObjectNotFoundException, BusinessObjectDeleteException, BusinessObjectSecurityException {

        if (list == null) {
            log.error("list must not be null");
            throw new NullPointerException();
        }
        int i = 0;
        for (BODTO<T> to : list) {

            try {
                T entity = (T) manager.find(getBasicService().getEntityClass(),
                                            to.getId());

                if (!checkClient(entity)) {
                    throw new BusinessObjectSecurityException(getCallersUser());
                }

                User user = getCallersUser();
                log.info(user.getName() + " deleted: "
                         + entity.toDescriptiveString());
                manager.remove(entity);

                if (i++ % 30 == 0) {
                    manager.flush();
                    manager.clear();
                }
            } catch (IllegalArgumentException iaex) {
                log.error(iaex.getMessage(), iaex);
                throw new BusinessObjectNotFoundException();
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                throw new BusinessObjectDeleteException();
            }



        }


    }


    /*
     * (non-Javadoc)
     *
     * @see de.linogistix.los.crud.BusinessObjectCRUDRemote#update(T)
     */
    @SuppressWarnings("unchecked")
    public void update(T entity) throws BusinessObjectNotFoundException,
        BusinessObjectModifiedException, BusinessObjectMergeException,
        BusinessObjectSecurityException, FacadeException {

        if (entity == null) {
            log.error("entity must not be null");
            throw new BusinessObjectNotFoundException();
        }
        User user = getCallersUser();
        if (!checkClient(entity)) {
            throw new BusinessObjectSecurityException(user);
        }

        T old = (T) manager.find(entity.getClass(), entity.getId());
        log.info(user.getName() + " updated: " + entity.toDescriptiveString()
                 + " *** was: " + old.toDescriptiveString());

        manager.merge(entity);

    }

    /*
     * (non-Javadoc)
     *
     * @see de.linogistix.los.crud.BusinessObjectCRUDRemote#retrieve(long)
     */
    @SuppressWarnings("unchecked")
    public T retrieve(long id) throws BusinessObjectNotFoundException,
        BusinessObjectSecurityException {

        BasicEntity entity;

        try {
            entity = (BasicEntity) manager.find(tClass, id);

            if (entity == null)
                throw new BusinessObjectNotFoundException(id, tClass);

            if (entity instanceof BasicClientAssignedEntity) {
                if (!((BasicClientAssignedEntity) entity).getClient().equals(
                            getCallersUser().getClient())) {
                    throw new BusinessObjectSecurityException(getCallersUser());
                }
            }

            return (T) eagerRead(entity);

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new BusinessObjectNotFoundException(id, tClass);
        }
    }

    @SuppressWarnings("unchecked")
    public void lock(T entity, int lock, String lockCause) throws BusinessObjectSecurityException {

        User user = getCallersUser();
        if (!checkClient(entity)) {
            throw new BusinessObjectSecurityException(user);
        }
        entity = (T) manager.find(tClass, entity.getId());
        entity.setLock(lock);

        entity.addAdditionalContent( "L  "+user.getName() + " : " + lockCause);

        log.info(user.getName() + " lock state changed: " + entity.toDescriptiveString());

    }


    @SuppressWarnings("unchecked")
    public void completeWorkVehicle(T entity, boolean status, String remarks) throws 
    	BusinessObjectNotFoundException, BusinessObjectDeleteException, BusinessObjectSecurityException {

        User user = getCallersUser();
        if (!checkClient(entity)) {
            throw new BusinessObjectSecurityException(user);
        }
        entity = (T) manager.find(tClass, entity.getId());

        WorkVehicleHistory wvh = new WorkVehicleHistory();
        WorkVehicle wv = (WorkVehicle) entity;
        wvh.setVehicleDataId(wv.getVehicleDataId());
        wvh.setRemarks(wv.getRemarks());
        wvh.setWorkTypeId(wv.getWorkTypeId());
        wvh.setWorkerId(wv.getWorkerId());
        wvh.setUrgent(wv.isUrgent());
        wvh.setScheduleTime(wv.getScheduleTime());
        wvh.setExecuteDeadline(wv.getExecuteDeadline());

        wvh.setCompletionRemarks(remarks);
        wvh.setCompletionSuccess(status);
        Date now = new Date();
        wvh.setCompletionDate(now);

        manager.persist(wvh);
        delete(entity);

	//log.info(user.getName() + " WorkVehicle completed: " + entity.toDescriptiveString());
    }

    @SuppressWarnings("unchecked")
    public void completeWorkItem(T entity, boolean status, String remarks) throws 
    	BusinessObjectNotFoundException, BusinessObjectDeleteException, BusinessObjectSecurityException {

        User user = getCallersUser();
        if (!checkClient(entity)) {
            throw new BusinessObjectSecurityException(user);
        }
        entity = (T) manager.find(tClass, entity.getId());

        WorkItemHistory wvh = new WorkItemHistory();
        WorkItem wv = (WorkItem) entity;
        wvh.setItemDataId(wv.getItemDataId());
        wvh.setRemarks(wv.getRemarks());
        wvh.setWorkTypeId(wv.getWorkTypeId());
        wvh.setWorkerId(wv.getWorkerId());
        wvh.setUrgent(wv.isUrgent());
        wvh.setScheduleTime(wv.getScheduleTime());
        wvh.setExecuteDeadline(wv.getExecuteDeadline());

        wvh.setCompletionRemarks(remarks);
        wvh.setCompletionSuccess(status);
        Date now = new Date();
        wvh.setCompletionDate(now);

        manager.persist(wvh);
	delete(entity);

        log.info(user.getName() + " WorkItem completed");
	//log.info(user.getName() + " WorkItem completed: " + entity.toDescriptiveString());
    }

    /**
     * Overide to gain performance. This method uses reflections. In an extended
     * class where the type <code><T></code> of BasicEntity is known you can
     * copy property values directly.
     *
     */
    public void mergeInto(T from, T to) throws BusinessObjectMergeException,
        BusinessObjectSecurityException {
        BasicEntityMerger<T> merger = new BasicEntityMerger<T>();
        try {
            merger.mergeInto(from, to);
        } catch (BasicEntityMergeException ex) {
            throw new BusinessObjectMergeException();
        }
    }

    // -------------------------------------------------------------------------

    protected boolean checkClient(BasicEntity entity) {
        return new BusinessObjectHelper(this.ctx, this.userService,
                                        this.context).checkClient(entity);
    }

    protected User getCallersUser() {
        return new BusinessObjectHelper(this.ctx, this.userService,
                                        this.context).getCallersUser();
    }

    protected BasicEntity eagerRead(BasicEntity entity) {
        return BusinessObjectHelper.eagerRead(entity);
    }


}

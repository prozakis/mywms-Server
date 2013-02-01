/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.model.UnitLoad;
import org.mywms.model.UnitLoadType;

/**
 * @see org.mywms.service.UnitLoadService
 * @author Taieb El Fakiri
 * @version $Revision: 560 $ provided by $Author: mjordan $
 */
@Stateless
public class UnitLoadServiceBean
    extends BasicServiceBean<UnitLoad>
    implements UnitLoadService
{
	
    /**
     * @see org.mywms.service.UnitLoadService#getListByUnitLoadType(Client,
     *      UnitLoadType)
     */
    @SuppressWarnings("unchecked")
    public List<UnitLoad> getListByUnitLoadType(Client client, UnitLoadType type)
    {
        StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT ul FROM " + UnitLoad.class.getSimpleName() + " ul ")
            .append("WHERE ul.type=:type ");

        if (!client.isSystemClient()) {
            qstr.append("AND ul.client = :client ");
        }

        qstr.append("ORDER BY ul.labelId ASC");

        Query query = manager.createQuery(qstr.toString());
        query.setParameter("type", type);

        if (!client.isSystemClient()) {
            query.setParameter("client", client);
        }

        return (List<UnitLoad>) query.getResultList();
    }

    /**
     * @see org.mywms.service.UnitLoadService#getByLabelId(Client,
     *      String)
     */
    public UnitLoad getByLabelId(Client client, String labelId)
        throws EntityNotFoundException
    {
        Query query =
            manager.createQuery("SELECT ul FROM "
                + UnitLoad.class.getSimpleName()
                + " ul "
                + "WHERE ul.labelId=:label "
                + "AND ul.client=:cl");

        query.setParameter("label", labelId);
        query.setParameter("cl", client);

        try {
            UnitLoad ul = (UnitLoad) query.getSingleResult();
            return ul;
        }
        catch (NoResultException ex) {
            throw new EntityNotFoundException(
                ServiceExceptionKey.NO_UNITLOAD_WITH_LABEL);
        }
    }

}

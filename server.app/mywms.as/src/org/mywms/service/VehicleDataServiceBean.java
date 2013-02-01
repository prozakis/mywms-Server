package org.mywms.service;

//import java.util.ArrayList;
import java.util.List;

//import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
//import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
//import org.mywms.model.Client;
import org.mywms.model.VehicleData;
//import org.mywms.model.ItemUnit;
//import org.mywms.model.StockUnit;
//import org.mywms.model.Zone;

@Stateless
public class VehicleDataServiceBean
    extends BasicServiceBean<VehicleData>
    implements VehicleDataService, VehicleDataServiceRemote
{
	private static final Logger log  = Logger.getLogger(VehicleDataServiceBean.class);

	/*public VehicleData getByLabelId(String labelId){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT vd FROM ");
		sb.append(VehicleData.class.getSimpleName()+ " vd ");
		sb.append("WHERE vd.labelId=:labelId ");
		
		Query query = manager.createQuery(sb.toString());
		
		query.setParameter("labelId", labelId);

		List<VehicleData> vdList = null;
		try {
			vdList = query.getResultList();
		}
		catch (NoResultException ex) {
			// is handled below
		}
		
		if( vdList != null && vdList.size() == 1 ) {
			return vdList.get(0);
		}
		
		return null;
	}*/
    
    public VehicleData create()
    {
        VehicleData vehicleData = new VehicleData();
        manager.persist(vehicleData);

        log.info("CREATED VehicleData: " + vehicleData.toDescriptiveString());
        
		manager.flush();

        return vehicleData;
    }
    
	public VehicleData getByPlateNumber(String plateNumber) throws EntityNotFoundException{
        Query query =
            manager.createQuery("SELECT su FROM "
                + VehicleData.class.getSimpleName()
                + " su "
                + " WHERE su.plateNumber= :adId");
        query = query.setParameter("adId", plateNumber);

        try{
        	VehicleData ret = (VehicleData) query.getSingleResult();
        	return ret;
        } catch (NoResultException ex){
        	throw new EntityNotFoundException(ServiceExceptionKey.NO_ENTITY_WITH_NAME);
        }      
    }
	
	public VehicleData getByLabelId(String labelId) throws EntityNotFoundException{
        Query query =
            manager.createQuery("SELECT su FROM "
                    + VehicleData.class.getSimpleName()
                    + " su "
                    + " WHERE su.labelId= :adId");
            query = query.setParameter("adId", labelId);

        try{
        	VehicleData ret = (VehicleData) query.getSingleResult();
        	return ret;
        } catch (NoResultException ex){
        	throw new EntityNotFoundException(ServiceExceptionKey.NO_ENTITY_WITH_NAME);
        }      
    }

    public String getPlateNumberByLabelId(String labelId) throws EntityNotFoundException{
        Query query =
            manager.createQuery("SELECT su FROM "
                + VehicleData.class.getSimpleName()
                + " su "
                + " WHERE su.labelId= :adId");
        query = query.setParameter("adId", labelId);

        try{
        	VehicleData ret = (VehicleData) query.getSingleResult();            	            	
        	return ret.getPlateNumber();
        } catch (NoResultException ex){
        	throw new EntityNotFoundException(ServiceExceptionKey.NO_ENTITY_WITH_NAME);
        }
    }
}

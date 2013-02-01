package de.linogistix.los.inventory.query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mywms.model.VehicleData;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.inventory.model.LOSFuelOrderLog;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.query.dto.LOSFuelOrderLogTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.BusinessObjectHelper;

@Stateless
public class LOSFuelOrderLogQueryBean extends
    BusinessObjectQueryBean<LOSFuelOrderLog> implements
    LOSFuelOrderLogQueryRemote {

		private static final String[] props = new String[] {
			"id", "version", "transactionId",
				"transactionDate",
				"storLocName", 
				"stationPump",				 				
				"orderType",  
				"vehiclePlateNumber",
				"receipientName",
				"receipientIDCard",
				"rcptArticleRef",
				"rcptArticleDescr",
				"rcptPosQuantity",
				"tankRemaining"				
		};

    @Override
    public String getUniqueNameProp() {
        return "transactionId";
    }

    @Override
    public Class<LOSFuelOrderLogTO> getBODTOClass() {
        return LOSFuelOrderLogTO.class;
    }

    @Override
    protected String[] getBODTOConstructorProps() {
		return props;
		//return new String[]{};
    }
	/*@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		List<BODTOConstructorProperty> propList = super.getBODTOConstructorProperties();
		
		propList.add(new BODTOConstructorProperty("id", false));
		propList.add(new BODTOConstructorProperty("version", false));
		propList.add(new BODTOConstructorProperty("transactionId", false));
		propList.add(new BODTOConstructorProperty("transactionDate", true));
		propList.add(new BODTOConstructorProperty("storLocName", true));
		propList.add(new BODTOConstructorProperty("stationPump", true));
		propList.add(new BODTOConstructorProperty("orderType", true));
		propList.add(new BODTOConstructorProperty("vehiclePlateNumber", true));
		propList.add(new BODTOConstructorProperty("receipientName", true));
		propList.add(new BODTOConstructorProperty("receipientIDCard", true));
		propList.add(new BODTOConstructorProperty("rcptArticleRef", true));
		propList.add(new BODTOConstructorProperty("articleDescr", true));	
		
		propList.add(new BODTOConstructorProperty("tankRemaining", true));
			
		return propList;
	}*/

    @Override
    protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
        List<TemplateQueryWhereToken> ret = new ArrayList<TemplateQueryWhereToken>();

        TemplateQueryWhereToken transactionId = new TemplateQueryWhereToken(
            TemplateQueryWhereToken.OPERATOR_LIKE, "transactionId",
            value);
        transactionId.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
        ret.add(transactionId);

        return ret;
    }

    @SuppressWarnings("unchecked")
    public List<LOSFuelOrderLog> queryByDate(Date createDateFrom,Date createDateTo) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ");
		buffer.append(" r ");
		buffer.append(" FROM ");
		buffer.append(LOSFuelOrderLog.class.getName());
		buffer.append(" r ");
		buffer.append(" WHERE ");
		//buffer.append(" r.created.date>=:dateFrom and r.created.date<=:dateTo");
		buffer.append(" r.created>=:dateFrom and r.created<=:dateTo");
		Query q = manager.createQuery(new String(buffer));
		q = q.setParameter("dateFrom", createDateFrom);
		q = q.setParameter("dateTo", createDateTo);
		
		return q.getResultList();
	}
	
    
    @SuppressWarnings("unchecked")
	public List<LOSFuelOrderLog> queryByDateAndLoc(Date createDateFrom,Date createDateTo, LOSStorageLocation storageLocation){
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ");
		buffer.append(" r ");
		buffer.append(" FROM ");
		buffer.append(LOSFuelOrderLog.class.getName());
		buffer.append(" r ");
		buffer.append(" WHERE ");
		buffer.append(" r.created>=:dateFrom and r.created<=:dateTo and r.storLocID=:locid");
		Query q = manager.createQuery(new String(buffer));
		q = q.setParameter("dateFrom", createDateFrom);
		q = q.setParameter("dateTo", createDateTo);
		q = q.setParameter("locid", storageLocation.getId());
		
		return q.getResultList();
	}
}

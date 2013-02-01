/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.reference.customization.inventory;

import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.ItemUnit;
import org.mywms.model.ItemUnitType;
import org.mywms.model.UnitLoadType;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.ItemDataService;
import org.mywms.service.ItemUnitService;
import org.mywms.service.UniqueConstraintViolatedException;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.common.service.QueryClientService;
import de.linogistix.los.customization.ImportDataServiceDispatcher;
import de.linogistix.los.inventory.query.ItemDataQueryRemote;
import de.linogistix.los.location.entityservice.LOSFixedLocationAssignmentService;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeService;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSRackLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.model.LOSUnitLoadPackageType;
import de.linogistix.los.location.query.LOSAreaQueryRemote;
import de.linogistix.los.location.query.LOSRackLocationQueryRemote;
import de.linogistix.los.location.query.LOSTypeCapacityConstraintQueryRemote;
import de.linogistix.los.location.query.RackQueryRemote;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;

@Stateless
public class Ref_DataServiceDispatcherBean 
implements ImportDataServiceDispatcher 
{
	
	private static final Logger logger = Logger.getLogger(Ref_DataServiceDispatcherBean.class);

	@EJB
	private ItemDataService itemService;
	
	@EJB
	private ItemUnitService itemUnitService;
	
	@EJB
	private ClientService clientService;
	
	@EJB
	private QueryClientService clientQuery;
	
	@EJB
	private LOSRackLocationQueryRemote rackLocationQuery; 
	
	@EJB
	private RackQueryRemote rackQuery;
		
	@EJB
	private LOSStorageLocationTypeService locationTypeService;

	@EJB
	private LOSAreaQueryRemote areaQuery;
	
	@EJB
	private LOSTypeCapacityConstraintQueryRemote capacityConstrQuery;
	
	@EJB
	private ItemDataQueryRemote idatQuery;
		
	@EJB
	private QueryUnitLoadTypeService ulTypeService;

	@EJB
	private LOSFixedLocationAssignmentService fixedService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	public Object handleDataRecord(String className, HashMap<String, String> dataRecord) throws FacadeException{
		
		Client sysClient = clientService.getSystemClient();
		
		StringBuffer log = new StringBuffer("Import " + className + " ");
		
		for(String key:dataRecord.keySet()){
			log.append(key+"="+dataRecord.get(key)+", ");
		}
		
		logger.info(log.toString());

		try {
			if(className.equals(ItemData.class.getSimpleName())){
				importItemData(className, dataRecord, sysClient, log);
			}
			else if (className.equals(ItemUnit.class.getSimpleName())){
				importItemUnit(className, dataRecord, sysClient, log);
			}
			else if(className.equals(LOSRackLocation.class.getSimpleName())){
				importRackLocation(className, dataRecord, sysClient, log);
			}
			else{
				logger.error("Not supported: " + className);
			}
		}
		catch( Throwable t ) {
			logger.error("Throwable: " + t.getMessage(), t);
		}
		return "Done";
	}

	private void importRackLocation(String className,
			HashMap<String, String> dataRecord, Client sysClient,
			StringBuffer log) throws BusinessObjectNotFoundException {

		
		// ----------- Properties -------------------------------------
		
		String clientNumber = dataRecord.get("Mandant".toLowerCase()); 
		
		String region = dataRecord.get("Halle".toLowerCase());
		
		String aisleNo = dataRecord.get("Gasse".toLowerCase());
		
		String rackNo = dataRecord.get("Regal".toLowerCase());
		
		String levelNo = dataRecord.get("Ebene".toLowerCase());
		
		String placeNo = dataRecord.get("Fach".toLowerCase());
		
		String fixedAssignedItem = dataRecord.get("Festplatzartikel".toLowerCase());
		
		String locTypeName = dataRecord.get("Fachtyp".toLowerCase());
		
		String areaName = dataRecord.get("Bereich".toLowerCase());
		
		String capacityConstrName = dataRecord.get("Kapazitätsbeschränkung".toLowerCase());
		
		//-------------------- assertions ----------------------------------
		
		if (region == null || region.length()== 0){
			logger.error("No region.");
			region = "";
		}
		if (aisleNo == null || aisleNo.length()== 0){
			logger.error("No aisleNo.");
			aisleNo = "";
		}
		if (rackNo == null || rackNo.length()== 0){
			logger.error("No rackNo.");
			rackNo = "";
		}
		if (region.length()==0 && aisleNo.length()==0 && rackNo.length()==0 ) {
			logger.error("No region, aisleNo and rackNo. Skip.");
			return;
		}
		
		if (levelNo == null || levelNo.length()== 0){
			logger.error("No levelNo. Skip.");
			return;
		}
		if (placeNo == null || placeNo.length()== 0){
			logger.error("No placeNo. Skip.");
			return;
		}
		
		// ----------- Client -------------------------------------
		
		Client c = null;
		try {
			c = clientQuery.getByNumber(clientNumber);
		} catch (UnAuthorizedException e) {
			// is checked on other places
		}
		if( c == null ) {
			logger.error("Client " + clientNumber + " does not exist. will use system client.");
			c = sysClient;
		}
		
		// ----------- Location type -------------------------------------
		
		LOSStorageLocationType locType;

		try {
			locType = locationTypeService.getByName(locTypeName);
		} catch (EntityNotFoundException ex) {
			logger.error("Unknown locTypeName: " + locTypeName + ". Try default");
			locType = locationTypeService.getDefaultStorageLocationType();
		}
		
		// ----------- Capacity Constraint -------------------------------------
		
		LOSTypeCapacityConstraint capacityConstraint;

		try {
			capacityConstraint = capacityConstrQuery.queryByIdentity(capacityConstrName);
		} catch (BusinessObjectNotFoundException ex) {
			logger.error("Unknown capacityConstraint: " + capacityConstrName + ". Won't set.");
			capacityConstraint = null;
		}
		
		// ----------- dummy unitload type for picking--------------------------------
		
		UnitLoadType ulTypeDummyPick;
		

		
			ulTypeDummyPick = ulTypeService.getPickLocationUnitLoadType();
		if (ulTypeDummyPick == null){
			logger.error("getPickLocationUnitLoadType not found!" + " Won't set.");
			ulTypeDummyPick = null;
		}
			
		// ----------- Area -------------------------------------
		
		LOSArea area;
		try {
			area = areaQuery.queryByIdentity(areaName);
		} catch (BusinessObjectNotFoundException ex) {
			// logger.error("Unknown areaName: " + areaName + ". Take default.");
			// Who is default???
			// area = areaService.getByType(LOSAreaType.STORE).get(0);
			
			logger.error("Unknown area " + areaName + ". => Skip." );
			return;
			
		}
		
		// ----------- Item Data -------------------------------------
		
		ItemData idat;
		try {
			if (fixedAssignedItem != null && fixedAssignedItem.length()!=0){
				idat = idatQuery.queryByIdentity(fixedAssignedItem);
			} else {
				idat = null;
			}
		} catch (BusinessObjectNotFoundException ex) {
			logger.error("Unknown idat: " + fixedAssignedItem );
			idat = null;
		}
		
		// ----------- Rack -------------------------------------
		
		LOSRack rack;
		String rackName = region + aisleNo + rackNo;

		try {
			rack = rackQuery.queryByIdentity(rackName);
		} catch (BusinessObjectNotFoundException ex) {

			rack = new LOSRack();
			rack.setName(rackName);
			rack.setLabelOffset(new Integer(3));
			rack.setClient(c);
			manager.persist(rack);
			manager.flush();
		}
			
		// ----------- Rack Location -------------------------------------
		
		LOSRackLocation rl;
		int y = 0;
		try {
			y = Integer.parseInt(levelNo);
		}
		catch( Throwable t ) {}
		
		int x = 0;
		try {
			x = Integer.parseInt(placeNo);
		}
		catch( Throwable t ) {}
		
//		String locName = rack.getName() + "-" + levelNo + "-" + placeNo;
		String locName = rack.getName() + "-" + placeNo + "-" + levelNo;
		
		try {
			rl = (LOSRackLocation) rackLocationQuery.queryByIdentity(locName);
			logger.warn("Already exists: " + locName);
		} catch (BusinessObjectNotFoundException ex) {
			rl = new LOSRackLocation();
			rl.setClient(c);
			rl.setArea(area);
			rl.setName(locName);
			rl.setRack(rack);
			rl.setType(locType);
			
			rl.setCurrentTypeCapacityConstraint(capacityConstraint);

			rl.setXPos(x);
			rl.setYPos(y);
			manager.persist(rl);
		}

		if (area.getAreaType().equals(LOSAreaType.PICKING)){
			LOSUnitLoad ul = new LOSUnitLoad();
			ul.setClient(c);
			ul.setLabelId(locName);
			ul.setType(ulTypeDummyPick);
			ul.setPackageType(LOSUnitLoadPackageType.OF_SAME_LOT_CONSOLIDATE);
			ul.setStorageLocation(rl);
			manager.persist(ul);
		}
		
		if (idat != null){
			// first remove old assignments
			List<LOSFixedLocationAssignment> flList = fixedService.getByItemData(idat);
			for( LOSFixedLocationAssignment flOld : flList ) {
				manager.remove(flOld);
			}
			LOSFixedLocationAssignment flOld = fixedService.getByLocation(rl);
			if( flOld != null ) {
				manager.remove(flOld);
			}
			manager.flush();
			
			LOSFixedLocationAssignment ass = new LOSFixedLocationAssignment();
			ass.setAssignedLocation(rl);
			ass.setItemData(idat);
			manager.persist(ass);
		}
		
		rack.addRackLocation(rl);
		
		manager.flush();
		manager.clear();
	}

	private void importItemUnit(String className,
			HashMap<String, String> dataRecord, Client sysClient, StringBuffer log) {
		ItemUnit unit;
		String unitName = dataRecord.get("name");
		
		if(unitName == null || unitName.equals("")){
			logger.error("--- Ignoring data set : ITEM UNIT EMPTY : "+log.toString());
			return;
		}
		
		unit = itemUnitService.getByName(unitName);
		if( unit != null ) {
			logger.error("ItemUnit: " + unitName + " already exists. Skip");
			return;
			
		}
		
		ItemUnitType unitType = null;
		String typeName = dataRecord.get("type");
		
		if(typeName == null || typeName.equals("")){
			logger.error("--- Ignoring data set : ITEM UNIT TYPE EMPTY : "+log.toString());
			return;
		}
		
		if(typeName.equals(ItemUnitType.LENGTH.name())){
			unitType = ItemUnitType.LENGTH;
		}
		else if(typeName.equals(ItemUnitType.WEIGHT.name())){
			unitType = ItemUnitType.WEIGHT;
		}
		
		ItemUnit base = null;
		String baseString = dataRecord.get("base");

		if(!baseString.equals("null")){
			base = itemUnitService.getByName(baseString);
			if( base == null ) {
				logger.error("--- Ignoring data set : UNKOWN BASE UNIT : "+log.toString());
				return;
			}
		}
		String factorString = dataRecord.get("faktor");
		
		unit = new ItemUnit();
		unit.setUnitName(unitName);
		unit.setUnitType(unitType);
		unit.setBaseUnit(base);
		unit.setBaseFactor(Integer.parseInt(factorString));
		
		manager.persist(unit);
		
		manager.flush();
		
	}

	private void importItemData(String className,
			HashMap<String, String> dataRecord, Client sysClient, StringBuffer log) {
		
		Client client = null;
		String clientNumber = dataRecord.get("mandant");
		if (clientNumber != null && clientNumber.length() > 0){
			try {
				client = clientQuery.getByNumber(clientNumber);
			} catch (UnAuthorizedException e) {
				// is checked on other places
			}
			if( client == null ) {
				logger.error("Client " + clientNumber + " does not exist. => Skip");
				return;
			}
		}
		else {
			logger.info("Use system client.");
			client = sysClient;
		}
		
		String number = dataRecord.get("nummer");
		if (number == null || number.length()== 0){
			logger.error("No number. Skip.");
			return;
		}
		
		String name = dataRecord.get("name");
		if (name == null || name.length()== 0){
			logger.info("No name. => use number as name.");
			name = number;
		}
		
		String descr = dataRecord.get("beschreibung");

		UnitLoadType ulType = null;;
		String unitLoadTypeName = dataRecord.get("lhm-typ");
		if (unitLoadTypeName != null && unitLoadTypeName.length() > 0){
			ulTypeService.getDefaultUnitLoadType();
			ulType = ulTypeService.getByName(unitLoadTypeName);
			if( ulType == null ) {
				logger.error("UnitLoadType " + unitLoadTypeName + " does not exist. => Skip.");
				return;
			}
		}
		if( ulType == null ) {
			logger.info("No unitLoadType. => Leave empty.");
		}

		
		boolean lotMandatory = false;
		String lotField = dataRecord.get("chargenpflicht");
		if( lotField != null ) {
			lotField = lotField.toLowerCase();
			if ("ja".equals(lotField)
					|| "yes".equals(lotField)
					|| "true".equals(lotField)
					|| "1".equals(lotField) ) {
				lotMandatory = true;
			}
		}		
		ItemUnit unit = null;
		String unitName = dataRecord.get("einheit");
		if( unitName != null && unitName.length()>0 ) {
			unit = itemUnitService.getByName(unitName);
			if( unit == null ) {
				logger.error("Unknown unit: " + unitName + ". does not exist. => Skip.");
		        return;
			}
		}
		if( unit == null ) {
			unit = itemUnitService.getDefault();
		}

		
		ItemData item;
		
		item = itemService.getByItemNumber(client, number);
		if (item == null) {
			try {
				item = itemService.create(client, number);
			} catch (UniqueConstraintViolatedException e) {
				logger.error("ItemData already exists. => Skip.");
		        return;
			}
		}
		else {
			logger.info("ItemData " + number + " already exists. => UPDATE");
		}
		item.setName(name);
		item.setDescription(descr);
		item.setDefaultUnitLoadType(ulType);
	    item.setLotMandatory(lotMandatory);
	    item.setHandlingUnit(unit);

	    manager.flush();
	    
		logger.info("ItemData " + number + " done.");
	}

}

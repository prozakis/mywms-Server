package de.linogistix.mobile.processes.fuel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.VehicleData;
import org.mywms.model.UnitLoadType;
import org.mywms.service.VehicleDataServiceRemote;
import org.mywms.globals.FuelOrderType;

import de.linogistix.los.inventory.businessservice.OrderBusiness;
import de.linogistix.los.inventory.facade.OrderFacade;
import de.linogistix.los.inventory.facade.OrderPositionTO;
import de.linogistix.los.inventory.facade.LOSGoodsReceiptFacade;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import de.linogistix.los.inventory.model.OrderType;
import de.linogistix.los.inventory.model.OrderReceiptPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSOrderReceipients;
import de.linogistix.los.inventory.pick.facade.PickOrderFacade;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.service.LOSPickRequestPositionService;
import de.linogistix.los.inventory.service.QueryItemDataServiceRemote;
import de.linogistix.los.inventory.service.LOSOrderReceipientsServiceRemote;
import de.linogistix.los.inventory.service.LOSFuelOrderLogService;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.inventory.query.dto.StockUnitTO;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.service.QueryStorageLocationServiceRemote;
import de.linogistix.los.location.service.QueryUnitLoadTypeServiceRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.util.entityservice.LOSSystemPropertyServiceRemote;
import de.linogistix.mobile.common.gui.bean.BasicDialogBean;
import de.linogistix.mobile.common.system.JSFHelper;

public class FuelBean extends BasicDialogBean {
    Logger log = Logger.getLogger(FuelBean.class);

    public static final String MODE_OUT = "MODE_OUT";
    public static final String MODE_IN = "MODE_IN";

    private String currentMode;

    private BigDecimal currentAmount;
    private ItemData currentItemData;
    private VehicleData currentVehicleData;
    private UnitLoadType currentUnitLoadType;
    private LOSGoodsReceipt currentGoodsReceipt;
    private LOSGoodsReceiptPosition currentGoodsReceiptPosition;   

    private String inputCode;
    private String inputAmount;
    private LOSStorageLocation loc;

    private String inLocation;
    private String outLocation;
    private String driver;
    private String plateNumber;
    private LOSOrderReceipients receipient;

    private String selectedOrderType;
    private ArrayList<SelectItem> orderTypeList;
    private int selectedPump;
    private ArrayList<SelectItem> pumpList;

    private OrderBusiness orderBusiness;
    private LOSPickRequestPositionService pickRequestPositionService;
    private QueryUnitLoadTypeServiceRemote queryUltService;

    private LOSGoodsReceiptFacade goodsReceiptFacade;

    private ManageInventoryFacade manageInventoryFacade;

    private OrderFacade orderFacade;

    private QueryStorageLocationServiceRemote locService;

    private LOSOrderReceipientsServiceRemote receipientService;

    private LOSFuelOrderLogService lOSFuelOrderLogService;

    private LOSSystemPropertyServiceRemote propertyService;

    private StockUnitQueryRemote stockUnitQuery;

    private QueryItemDataServiceRemote queryItemData;

    private VehicleDataServiceRemote queryVehicleData;

    private PickOrderFacade pickOrderFacade;

    public FuelBean() {
        super();

        pickRequestPositionService= super.getStateless(LOSPickRequestPositionService.class);
        propertyService = super.getStateless(LOSSystemPropertyServiceRemote.class);
        queryUltService = super.getStateless(QueryUnitLoadTypeServiceRemote.class);
        goodsReceiptFacade = super.getStateless(LOSGoodsReceiptFacade.class);
        manageInventoryFacade = super.getStateless(ManageInventoryFacade.class);
        orderFacade = super.getStateless(OrderFacade.class);
        locService = super.getStateless(QueryStorageLocationServiceRemote.class);
        receipientService = super.getStateless(LOSOrderReceipientsServiceRemote.class);
        stockUnitQuery = super.getStateless(StockUnitQueryRemote.class);
        queryItemData = super.getStateless(QueryItemDataServiceRemote.class);
        queryVehicleData = super.getStateless(VehicleDataServiceRemote.class);
        pickOrderFacade = super.getStateless(PickOrderFacade.class);
        orderBusiness = super.getStateless(OrderBusiness.class);
        lOSFuelOrderLogService = super.getStateless(LOSFuelOrderLogService.class);
    }

    public String getNavigationKey() {
        if(!isRolesAllowed()){
        	currentMode = "";
            return FuelNavigationEnum.FUEL_BACK_TO_MENU.name();
		}

        if( currentMode == MODE_OUT ) {
            return FuelNavigationEnum.FUEL_CHOOSE_VEHICLE.name();
        }
        return FuelNavigationEnum.FUEL_CHOOSE_ITEM.name();
    }


    public String getTitle() {
        if( currentMode == MODE_OUT ) {
            return resolve("TitleFuelOut");
        }
        return resolve("TitleFuelIn");
    }

    @Override
    public void init(String[] args) {
        super.init(args);

        currentMode = MODE_OUT;

        if( args != null && args.length>0 ) {
            String s = args[0];
            if(s.startsWith(MODE_IN) ) {
                currentMode = MODE_IN;
            }
        }

    }

    public String processEnterItem() {
		if(currentMode != MODE_OUT){
			currentMode = MODE_IN;
		}

        String code = inputCode == null ? "" : inputCode.trim();
        inputCode = "";


        if( code.length() == 0 ) {
            JSFHelper.getInstance().message( resolve("MsgEnterMat") );
            return "";
        }

        ItemData mat = queryItemData.getByItemNumber(code);

        if( mat == null ) {
            JSFHelper.getInstance().message( resolve("MsgMatNotFound") );
            return "";
        }

        if( mat.isAdviceMandatory() ) {
            JSFHelper.getInstance().message( resolve("MsgMatAdviceMandatory") );
            return "";
        }

        currentItemData = mat;
        //initPos();

        //if( collectLotAlways || mat.isLotMandatory() ) {
        //return GRDirectNavigationEnum.GRD_ENTER_LOT.name();
        //}
        //else if( mat.getSerialNoRecordType() == SerialNoRecordType.ALWAYS_RECORD ) {
        //return GRDirectNavigationEnum.GRD_ENTER_SERIAL.name();
        //}

		//JSFHelper.getInstance().message(currentMode );
		//return "";
		return FuelNavigationEnum.FUEL_ENTER_AMOUNT.name();
    }

    public String processEnterItemCancel() {
        //reset();
        currentMode = "";
        return FuelNavigationEnum.FUEL_BACK_TO_MENU.name();
    }

    public String processEnterVeh() {
        currentMode = MODE_OUT;

        String code = inputCode == null ? "" : inputCode.trim();
        inputCode = "";

        if( code.length() == 0 ) {
            JSFHelper.getInstance().message( resolve("MsgEnterVeh") );
            return "";
        }

        VehicleData veh;
        try {
            veh = queryVehicleData.getByLabelId(code);
        } catch( Throwable t ) {
            log.error(resolve("MsgVehNotFound"));
            JSFHelper.getInstance().message( resolve("MsgVehNotFound") );
            return "";
        }

        if( veh == null ) {
            JSFHelper.getInstance().message( resolve("MsgVehNotFound") );
            return "";
        }


        currentVehicleData = veh;
        //initPos();

        //if( collectLotAlways || mat.isLotMandatory() ) {
        //return GRDirectNavigationEnum.GRD_ENTER_LOT.name();
        //}
        //else if( mat.getSerialNoRecordType() == SerialNoRecordType.ALWAYS_RECORD ) {
        //return GRDirectNavigationEnum.GRD_ENTER_SERIAL.name();
        //}

        return FuelNavigationEnum.FUEL_CHOOSE_ITEM.name();
    }

    public String processEnterVehCancel() {
        //reset();
        currentMode = "";
        return FuelNavigationEnum.FUEL_BACK_TO_MENU.name();
    }

    public String processEnterAmount() {
        inputAmount = inputAmount == null ? "" : inputAmount.trim();

        if( inputAmount.length() == 0 ) {
            JSFHelper.getInstance().message( resolve("MsgEnterAmount") );
            currentAmount = BigDecimal.ZERO;
            return "";
        }
        try {
            currentAmount = new BigDecimal(inputAmount);
        } catch( Throwable t ) {
            log.error("Cannot convert to BigDecimal code="+inputAmount);
            JSFHelper.getInstance().message( resolve("MsgAmountNotValid") );
            return "";
        }
        if( BigDecimal.ZERO.compareTo(currentAmount) >= 0 ) {
            log.error("Amount must be > 0. Amount="+currentAmount);
            JSFHelper.getInstance().message( resolve("MsgAmountNotValid") );
            currentAmount = BigDecimal.ZERO;
            return "";
        }

        if( currentMode == MODE_OUT )
            return FuelNavigationEnum.FUEL_ENTER_RECEIPIENT.name();
        else
            return FuelNavigationEnum.ENTER_TARGET_IN_LOC.name();
    }
    public String processEnterAmountCancel() {
        //initPos();
        //return getStartPage2();
        currentMode = "";
        return FuelNavigationEnum.FUEL_BACK_TO_MENU.name();
    }

    public String processEnterTargetLoc() {
        String code = inputCode == null ? "" : inputCode.trim();
        inputCode = "";

        if( code.length() == 0 ) {
            JSFHelper.getInstance().message( resolve("MsgEnterLoc") );
            return "";
        }

        loc = null;
        try {
            loc = locService.getByName(code);
        } catch( Exception e ) {
            log.error("Cannot select location="+code+". ex="+e.getMessage(), e);
        }
        if( loc == null ) {
            log.error("Wrong location entered. location="+code);
            JSFHelper.getInstance().message( resolve("MsgLocNotAccessable") );
            return "";
        }

        //return postUnitLoad( loc.getName(), null );
        return FuelNavigationEnum.FUEL_ENTER_DELIVERER.name();

    }

    public String processEnterTargetCancel() {
        //initPos();
        //return getStartPage2();
        currentMode = "";
        return FuelNavigationEnum.FUEL_BACK_TO_MENU.name();
    }

    public String processEnterOriginLoc() {
        String code = inputCode == null ? "" : inputCode.trim();
        inputCode = "";
        LOSStorageLocation outLoc = null;

        if( code.length() == 0 ) {
            JSFHelper.getInstance().message( resolve("MsgEnterLoc") );
            return "";
        }
        loc = null;
        try {
            loc = locService.getByName(code);
        } catch( Exception e ) {
            log.error("Cannot select location="+code+". ex="+e.getMessage(), e);
        }
        if( loc == null ) {
            log.error("Wrong location entered. location="+code);
            JSFHelper.getInstance().message( resolve("MsgLocNotAccessable") );
            return "";
        }

		outLocation = code+"OUT";
        //outLocation = "Nirwana";
        try {
            outLoc = locService.getByName(outLocation);
        } catch( Exception e ) {
            log.error("Output location "+outLocation+" does not exist. ex="+e.getMessage(), e);
        }
        if( outLoc == null ) {
            log.error("Output location "+outLocation+" does not exist");
            JSFHelper.getInstance().message( resolve("MsgOutLocNotAccessable") );
            return "";
        }
		inLocation = code;

        currentMode = "";
        return FuelNavigationEnum.FUEL_ENTER_PUMP.name();
    }

    public String processEnterOriginCancel() {
        //initPos();
        //return getStartPage2();
        currentMode = "";
        return FuelNavigationEnum.FUEL_BACK_TO_MENU.name();
    }

    public String processEnterDeliverer() {

        String drv = driver == null ? "" : driver.trim();
        String plate = plateNumber == null ? "" : plateNumber.trim();
        //driver="";
        //plateNumber="";

        if( drv.length() == 0 ) {
            JSFHelper.getInstance().message( resolve("MsgEnterDrv") );
            return "";
        }
        if( plate.length() == 0 ) {
            JSFHelper.getInstance().message( resolve("MsgEnterPlate") );
            return "";
        }

        return FuelNavigationEnum.FUEL_ENTER_RECEIPIENT.name();
    }

    public String processEnterDelivererCancel() {
        currentMode = "";
        return FuelNavigationEnum.FUEL_BACK_TO_MENU.name();
    }

    private String finalizeFuelIn() {
        Client c = currentItemData.getClient();
        BODTO<Client> cdto = new BODTO<Client>(c.getId(), c.getVersion(), c.getNumber());
        BODTO<LOSStorageLocation> inLocation = new BODTO<LOSStorageLocation>(loc.getId(),
                loc.getVersion(), loc.getName());
        BODTO<ItemData> it = new BODTO<ItemData>(currentItemData.getId(),
                currentItemData.getVersion(), currentItemData.getNumber());
        Date receiptDate = new Date();

        currentGoodsReceipt = goodsReceiptFacade.createGoodsReceipt(cdto,
                              plateNumber, driver, receipient.getIdentityCard(), "",
                              receiptDate, inLocation, "");
        

        if(currentGoodsReceipt == null) {
            log.error("Cannot create GoodsReceipt");
            JSFHelper.getInstance().message( resolve("MsgGoodsReceiptNull") );
            return "";
        }

      /*  
        try {
        	currentGoodsReceiptPosition = goodsReceiptFacade.createGoodsReceiptPosition(cdto, currentGoodsReceipt, null, it, currentGoodsReceipt.getDeliveryNoteNumber(),null,currentAmount,null);
         } catch (FacadeException ex) {
        	 log.error("Cannot create GoodsReceipt Position");
            JSFHelper.getInstance().message( resolve("MsgGoodsReceiptNull") );
            return "";
        }
        */
        
        try {
            goodsReceiptFacade.finishGoodsReceipt(currentGoodsReceipt);
        } catch (FacadeException ex) {
            log.error(ex, ex);
            JSFHelper.getInstance().message( resolve("MsgGoodsReceiptFacadeEx") );
            return "";
        }
                      
        
        LOSResultList<StockUnitTO> list = null;

        StockUnitTO oldSU;
        BigDecimal oldAmount;
                
        try {
            list = stockUnitQuery.queryByDefault(null, null, it, inLocation, new QueryDetail(0, Integer.MAX_VALUE));
            if(list.size()>0) {
                oldSU = list.get(0);
                oldAmount = oldSU.getAmount();
                oldAmount = oldAmount.add(currentAmount);

                manageInventoryFacade.changeAmount(oldSU,
                                                   oldAmount, new BigDecimal(0), "");

                /* getorcreateunitload */
		lOSFuelOrderLogService.create(loc, receipient, currentItemData, currentAmount, "FUEL ORDER IN", oldAmount);
        		currentMode = "";
                return FuelNavigationEnum.FUEL_IN_COMPLETE.name();
            }

        } catch (Throwable ex) {
            log.error(ex, ex);
            JSFHelper.getInstance().message( resolve("MsgUnknownError") );
            return "";
        }

        return postUnitLoad( loc.getName(), null );
    }

    public String processEnterReceipient() {
        String code = inputCode == null ? "" : inputCode.trim();
        inputCode = "";

        if( code.length() == 0 ) {
            JSFHelper.getInstance().message( resolve("MsgEnterReceipient") );
            return "";
        }

        receipient = null;

        try {
            receipient = receipientService.getByIdentityCard(code);
        } catch( Exception e ) {
            log.error("Cannot select receipient="+code+". ex="+e.getMessage(), e);
        }
        if( receipient == null ) {
            log.error("Wrong receipient entered. receipient="+code);
            JSFHelper.getInstance().message( resolve("MsgReceipientNotAccessable") );
            return "";
        }

        if( currentMode == MODE_OUT )
            return FuelNavigationEnum.FUEL_ENTER_ORDERTYPE.name();
        else
            return finalizeFuelIn();
    }

    public String processEnterReceipientCancel() {
        //reset();
        currentMode = "";
        return FuelNavigationEnum.FUEL_BACK_TO_MENU.name();
    }

    public String processEnterOrderType() {
        orderTypeList = null;

        if(selectedOrderType==null ||selectedOrderType.length() == 0) {
            JSFHelper.getInstance().message( resolve("MsgOrderTypeError") );
            return "";
        }

        return FuelNavigationEnum.ENTER_ORIGIN_LOC.name();
    }

    public String processEnterOrderTypeCancel() {
        currentMode = "";
        return FuelNavigationEnum.FUEL_BACK_TO_MENU.name();
    }

    public String processEnterPump() {
        currentMode = "";
        pumpList = null;

		if(selectedPump < 1) {
			selectedPump = 1;
		}


        LOSResultList<StockUnitTO> list = null;
        BODTO<LOSStorageLocation> sl = new BODTO<LOSStorageLocation>(loc.getId(),
                loc.getVersion(), loc.getName());
        BODTO<ItemData> it = new BODTO<ItemData>(currentItemData.getId(),
                currentItemData.getVersion(), currentItemData.getNumber());
        Client c = currentItemData.getClient();
        StockUnitTO oldSU;
        BigDecimal oldAmount = new BigDecimal(0);
        try {
            list = stockUnitQuery.queryByDefault(null, null, it, sl, new QueryDetail(0, Integer.MAX_VALUE));
            if(list.size()>0) {
                oldSU = list.get(0);
                oldAmount = oldSU.getAmount();
            }

        } catch (Throwable ex) {
            log.error(ex, ex);
            JSFHelper.getInstance().message( resolve("MsgUnknownError") );
            return "";
        }

        if(oldAmount.compareTo(currentAmount) == -1) {
            JSFHelper.getInstance().message( resolve("MsgAmountInsufficient") );
            return "";
        }
    	
        BigDecimal amountRemaining = oldAmount.subtract(currentAmount);
        
        if (amountRemaining.compareTo(loc.getType().getLowVolume()) == -1) {
            JSFHelper.getInstance().message( resolve("MsgLowVolume") );            
        	}
        
        if (amountRemaining.compareTo(loc.getType().getMinimumVolume()) == -1) {
            JSFHelper.getInstance().message( resolve("MsgMinimumVolume") );
            return "";
        }

        OrderPositionTO[] tos = new OrderPositionTO[1];
        OrderPositionTO to = new OrderPositionTO();
        to.amount = currentAmount;
        String val = currentItemData.getNumber();
        if( val != null && val.startsWith("* ") )
            val = val.substring(2);
        to.articleRef = val;
        to.batchRef = null;
        to.clientRef = c.getName();
        tos[0] = to;

        LOSPickRequest pr;
        LOSOrderRequest or;
        try {
            pr = orderFacade.orderFuel(c.getNumber(),
                                       null,
                                       tos,
                                       null,
                                       null,
                                       outLocation,
                                       OrderType.TO_CUSTOMER,
                                       new Date(),
                                       true,
                                       null);
        } catch (FacadeException e) {
            log.error(e,e);
            JSFHelper.getInstance().message( resolve("MsgOrderStartFail") );
            return "";
        }

        List<LOSPickRequestPosition> positionList = pickRequestPositionService.getByPickRequest(pr);
        int totalPosition = 1;
        totalPosition = positionList.size();

        boolean forward = false;
        for (int i = 0; i<totalPosition; i++) {
            if (positionList.get(i).isPicked() || positionList.get(i).isCanceled())
                continue;

            try {
                forward = pickOrderFacade.testCanProcess(positionList.get(i), false, inLocation, currentAmount);
            } catch (Throwable ex) {
                log.error(ex, ex);
                JSFHelper.getInstance().message( resolve("MsgUnknownError") );
                return "";
            }

            if(forward) {
                try {
                    pickOrderFacade.processPickRequestPosition(positionList.get(i), false, inLocation, currentAmount, false, false);
                } catch (Throwable ex) {
                    log.error(ex, ex);
                    JSFHelper.getInstance().message( resolve("MsgUnknownError") );
                    return "";
                }
            }
        }

        try {
            pr = pickOrderFacade.finishPickingRequest(pr, pr.getDestination().getName());
        } catch (Throwable ex) {
            log.error(ex, ex);
            JSFHelper.getInstance().message( resolve("MsgUnknownError") );
            return "";
        }

	OrderReceiptPosition orp;
        or = pr.getParentRequest();
        try {
            orp = orderBusiness.finishFuelOrder(or, false, receipient);
        } catch (Throwable ex) {
            log.error(ex, ex);
            JSFHelper.getInstance().message( resolve("MsgUnknownError") );
            return "";
        }

	lOSFuelOrderLogService.create(currentVehicleData, loc, selectedPump, receipient, orp, selectedOrderType, amountRemaining);

	return FuelNavigationEnum.FUEL_OUT_COMPLETE.name();
    }

    public String processEnterPumpCancel() {
        currentMode = "";
        return FuelNavigationEnum.FUEL_BACK_TO_MENU.name();
    }


    public String postUnitLoad( String targetLocName, String targetUlName ) {

        if( currentAmount == null || BigDecimal.ZERO.compareTo(currentAmount) >= 0 ) {
            log.error("Amount not valid. amount="+currentAmount);
            JSFHelper.getInstance().message( resolve("MsgAmountNotValid") );
        	currentMode = "";
            return FuelNavigationEnum.FUEL_BACK_TO_MENU.name();
        }

        //if( currentUnitLoadType == null ) {
        currentUnitLoadType = queryUltService.getDefaultUnitLoadType();
        //}

        //if( currentMode == MODE_MATERIAL ) {
        return postMaterial(targetLocName, targetUlName);
        //}
        //else {
        //return postAdvice(targetLocName, targetUlName);
        //}
    }

    public String postMaterial( String targetLocName, String targetUlName ) {

        int lock = (int)propertyService.getLongDefault(getWorkstationName(), "GOODS_IN_DEFAULT_LOCK", 0);
        if( currentItemData == null ) {
            JSFHelper.getInstance().message( resolve("MsgMatNotValid") );
            return "";
        }
        if( currentUnitLoadType == null ) {
            JSFHelper.getInstance().message( resolve("MsgUlTypeNotValid") );
            return "";
        }
        try {

            goodsReceiptFacade.createStock(
                currentItemData.getClient().getNumber(),
                null,
                currentItemData.getNumber(),
                null,
                //resolve("LabelGenerated"),
                currentUnitLoadType.getName(),
                currentAmount,
                lock, null, null, targetLocName, targetUlName );


            //currentAmountOfProcessedUnitLoads++;


        } catch ( FacadeException e) {
            log.error("Error in posting ("+getLocale()+"): "+e.getMessage());
            String msg = ((FacadeException) e).getLocalizedMessage( getLocale() );
            JSFHelper.getInstance().message(msg);
            return "";

        } catch ( Throwable e) {
            log.error("General Error in posting: "+e.getMessage(), e);
            String msg = e.getLocalizedMessage();
            JSFHelper.getInstance().message(msg);
            return "";
        }

	lOSFuelOrderLogService.create(null, loc, 0, receipient, null, "FUEL ORDER IN", currentAmount);

        currentMode = "";
        return FuelNavigationEnum.FUEL_IN_COMPLETE.name();
    }


    public String getInputCode() {
        return inputCode;
    }
    public void setInputCode(String inputCode) {
        this.inputCode = inputCode;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getItemDataNumber() {
        return currentItemData == null ? "" : currentItemData.getNumber();
    }

    public String getItemDataName() {
        return currentItemData == null ? "" : currentItemData.getName();
    }

    public String getAmount() {
        return currentAmount == null ? "" : currentAmount.toString() + " "+ getCurrentUnit();
    }

    public String getInputAmount() {
        return inputAmount;
    }
    public void setInputAmount(String inputAmount) {
        this.inputAmount = inputAmount;
    }

    public String getCurrentUnit() {
        if( currentItemData == null ) {
            return "";
        }
        return currentItemData.getHandlingUnit().getUnitName();
    }

    @Override
    protected ResourceBundle getResourceBundle() {
        ResourceBundle bundle;
        Locale lo;
        lo = getUIViewRoot().getLocale();
        bundle = ResourceBundle.getBundle("de.linogistix.mobile.processes.fuel.FuelBundle", lo);
        return bundle;
    }

    @Override
    public boolean isRolesAllowed() {
        String[] allowed;
        String[] roles;

        allowed = getRolesAllowed();
        if (allowed == null || allowed.length == 0) {
            return false;
        }

        roles = getPrincipalRoles();

        if ((allowed == null || allowed.length == 0)
                && (roles == null || roles.length == 0)) {
            return true;
        }

        for (String allow : allowed) {
            for (String role : roles) {
                if (allow.equals(role)) {
                    return true;
                }
            }
        }

        return false;

    }

    @Override
    public String[] getRolesAllowed () {
        //return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
        return new String[] {org.mywms.globals.Role.FUEL_STR};
    }

    public String getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(String currentMode) {
        this.currentMode = currentMode;
    }

    public LOSOrderReceipients getReceipient() {
        return receipient;
    }

    public void setReceipient(LOSOrderReceipients receipient) {
        this.receipient = receipient;
    }

    public String getSelectedOrderType() {
        return selectedOrderType;
    }

    public void setSelectedOrderType(String selectedOrderType) {
        this.selectedOrderType = selectedOrderType;
    }

    public List<SelectItem> getOrderTypeList() {

        if( orderTypeList == null ) {
            orderTypeList = new ArrayList<SelectItem>();

            for (FuelOrderType ot : FuelOrderType.values()) {
                orderTypeList.add(new SelectItem(ot, resolve(ot.getLabel())));
            }
        }

        return orderTypeList;
    }

    public int getSelectedPump() {
        return selectedPump;
    }

    public void setSelectedPump(int selectedPump) {
        this.selectedPump = selectedPump;
    }

    public List<SelectItem> getPumpList() {

        if( pumpList == null ) {
		int pumps=1;
		if( loc != null ){
			pumps = loc.getPickingSources();
		}
		if(pumps < 1)
			pumps=1;

            pumpList = new ArrayList<SelectItem>();

            for (int i =1; i<=pumps; i++) {
                pumpList.add(new SelectItem(i));
            }
        }

        return pumpList;
    }

    public String getOutLocation() {
        return outLocation;
    }

    public void setOutLocation(String outLocation) {
        this.outLocation = outLocation;
    }

    public String getInLocation() {
        return inLocation;
    }

    public void setInLocation(String inLocation) {
        this.inLocation = inLocation;
    }
    
    public VehicleData getCurrentVehicleData()
    {
        return currentVehicleData;
    }
    
    public void setCurrentVehicleData(VehicleData currentVehicleData)
    {
        this.currentVehicleData = currentVehicleData;
    }
    
    public LOSStorageLocation getLoc() {
		return loc;
	}

	public void setLoc(LOSStorageLocation loc) {
		this.loc = loc;
	}
    
    public BigDecimal getCurrentAmount()
    {
        return currentAmount;
    }
    
    public void setCurrentAmount(BigDecimal currentAmount)
    {
        this.currentAmount = currentAmount;
    }
    
}

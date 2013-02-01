/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 * www.linogistix.com
 * 
 * Project: myWMS-LOS
*/
package de.linogistix.mobile.processes.gr_direct;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.ItemUnitType;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoadType;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.businessservice.LOSGoodsReceiptComponent;
import de.linogistix.los.inventory.facade.LOSGoodsReceiptFacade;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.inventory.model.LOSGoodsReceiptType;
import de.linogistix.los.inventory.service.QueryAdviceServiceRemote;
import de.linogistix.los.inventory.service.QueryGoodsReceiptServiceRemote;
import de.linogistix.los.inventory.service.QueryItemDataServiceRemote;
import de.linogistix.los.inventory.service.QueryLotServiceRemote;
import de.linogistix.los.inventory.service.QueryStockServiceRemote;
import de.linogistix.los.inventory.service.dto.GoodsReceiptTO;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.UnitLoadQueryRemote;
import de.linogistix.los.location.service.QueryFixedAssignmentServiceRemote;
import de.linogistix.los.location.service.QueryStorageLocationServiceRemote;
import de.linogistix.los.location.service.QueryUnitLoadServiceRemote;
import de.linogistix.los.location.service.QueryUnitLoadTypeServiceRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.report.businessservice.ReportService;
import de.linogistix.los.util.DateHelper;
import de.linogistix.los.util.entityservice.LOSSystemPropertyServiceRemote;
import de.linogistix.mobile.common.gui.bean.BasicDialogBean;
import de.linogistix.mobile.common.system.JSFHelper;
/*
 * TODO krane
 * Storage on fixed location. Do not suggest it, when receiving a different lot
 * Start search of new location, on demand
 * Start search of unit load to add, on demand
 * Enable selection of existing lot
 */
/**
 * Mobile process: Goods receipt with direct storage.
 * May be used for simple goods receipt processes or returns.
 * 
 * @author krane
 *
 */
public class GRDirectBean extends BasicDialogBean {
	Logger log = Logger.getLogger(GRDirectBean.class);

	public static final String MODE_GOODS_RECEIPT = "MODE_GOODS_RECEIPT";
	public static final String MODE_MATERIAL = "MODE_MATERIAL";

	
	/**
	 * SystemProperty. Enable input of unit load type.
	 * If enabled, the user is asked for the type of a new unit load.
	 * Otherwise, the default unit load (material or system) type is used. 
	 */
	public static final String GRD_COLLECT_UNITLOAD_TYPE = "GRD_COLLECT_UNITLOAD_TYPE";
	private boolean collectUlType = true;
	
	/**
	 * SystemProperty. Enable input of unit load number.
	 * If enabled, the user is asked for the number of a new unit load.
	 * Otherwise, all numbers will be generated automatically. 
	 */
	public static final String GRD_COLLECT_UNITLOAD_NO = "GRD_COLLECT_UNITLOAD_NO";
	private boolean collectUlNo = true;
	
	/**
	 * SystemProperty. Enable automatically generation of unit load numbers.
	 * If enabled, it is possible to let the system generate unit load numbers for new unit loads (just leave the field empty).
	 * Otherwise, the input of a unit load number is mandatory. 
	 */
	public static final String GRD_GENERATE_UNITLOAD_NO = "GRD_GENERATE_UNITLOAD_NO";
	private boolean generateUlNo = true;
	
	/**
	 * SystemProperty. Enable automatically generation of unit load numbers.
	 * If enabled, a lot can be recorded for all materials. 
	 * Otherwise, the input of a lot is only possible for lot-mandatory materials. 
	 */
	public static final String GRD_COLLECT_LOT_ALWAYS = "GRD_COLLECT_LOT_ALWAYS";
	private boolean collectLotAlways = false;
	
	private boolean limitAmountToNotified = false;
	
	private String currentMode;
	


	
	
	private LOSStorageLocation currentFixTarget;
	private String inputLotName;
	private Date currentLotDate;
	private String currentUlLabel;
	private boolean currentUlLabelSelected;
	private BigDecimal currentAmount;
	private LOSAdvice currentAdvice;
	private Lot currentLot;
	private ItemData currentItemData;
	private UnitLoadType currentUnitLoadType;
	private int currentAmountOfProcessedUnitLoads = 0;
	private LOSGoodsReceipt currentGoodsReceipt;
	private String currentSerialNo;
	private String currentAddLocation;
		
	private String inputCode;
	private String inputAmount;

	private ArrayList<SelectItem> goodsReceiptList;
	private ArrayList<SelectItem> adviceList;
	private HashMap<String, UnitLoadType> unitLoadTypeMap;
	private Long selectedGoodsReceiptId;
	private Long selectedAdviceId;
	
	


	private QueryLotServiceRemote queryLotService;
	
	private QueryUnitLoadTypeServiceRemote queryUltService;
	
	private QueryUnitLoadServiceRemote queryUlService;

	private QueryAdviceServiceRemote queryAdviceService;
	
	private QueryGoodsReceiptServiceRemote queryGoodsReceiptService;
		
	private LOSGoodsReceiptFacade goodsReceiptFacade;
	
	private QueryStorageLocationServiceRemote locService;
	
	private QueryFixedAssignmentServiceRemote fixService;
	
	private LOSSystemPropertyServiceRemote propertyService;
	
	private QueryStockServiceRemote queryStockService;

	private UnitLoadQueryRemote queryUnitLoadRemote;
	
	private QueryItemDataServiceRemote queryItemData;
	
	
	public GRDirectBean(){
		super();
		
		propertyService = super.getStateless(LOSSystemPropertyServiceRemote.class);
		queryLotService = super.getStateless(QueryLotServiceRemote.class);
		queryAdviceService = super.getStateless(QueryAdviceServiceRemote.class);
		queryUltService = super.getStateless(QueryUnitLoadTypeServiceRemote.class);
		queryUlService = super.getStateless(QueryUnitLoadServiceRemote.class);
		queryGoodsReceiptService = super.getStateless(QueryGoodsReceiptServiceRemote.class);
		goodsReceiptFacade = super.getStateless(LOSGoodsReceiptFacade.class);
		fixService = super.getStateless(QueryFixedAssignmentServiceRemote.class);
		locService = super.getStateless(QueryStorageLocationServiceRemote.class);
		queryStockService = super.getStateless(QueryStockServiceRemote.class);
		queryUnitLoadRemote = super.getStateless(UnitLoadQueryRemote.class);
		queryItemData = super.getStateless(QueryItemDataServiceRemote.class);
		
		collectUlType = propertyService.getBooleanDefault(getWorkstationName(), GRD_COLLECT_UNITLOAD_TYPE, collectUlType);
		log.info(GRD_COLLECT_UNITLOAD_TYPE+"="+collectUlType);
		
		collectUlNo = propertyService.getBooleanDefault(getWorkstationName(), GRD_COLLECT_UNITLOAD_NO, collectUlNo);
		log.info(GRD_COLLECT_UNITLOAD_NO+"="+collectUlNo);
		
		generateUlNo = propertyService.getBooleanDefault(getWorkstationName(), GRD_GENERATE_UNITLOAD_NO, generateUlNo);
		log.info(GRD_GENERATE_UNITLOAD_NO+"="+generateUlNo);
		
		collectLotAlways = propertyService.getBooleanDefault(getWorkstationName(), GRD_COLLECT_LOT_ALWAYS, collectLotAlways);
		log.info(GRD_COLLECT_LOT_ALWAYS+"="+collectLotAlways);
		
		limitAmountToNotified = propertyService.getBooleanDefault(getWorkstationName(), LOSGoodsReceiptComponent.GR_LIMIT_AMOUNT_TO_NOTIFIED, limitAmountToNotified);
		log.info("GR_LIMIT_AMOUNT_TO_NOTIFIED="+limitAmountToNotified );
	}
	public String getNavigationKey() {
		if( currentMode == MODE_MATERIAL ) {
			return GRDirectNavigationEnum.GRD_ENTER_MAT.name();
		}
		return GRDirectNavigationEnum.GRD_CHOOSE_GR.name();
		
		
	}


	public String getTitle() {
		if( currentMode == MODE_MATERIAL ) {
			return resolve("TitleGRMat");
		}
		return resolve("TitleGRDirect");
	}
	
	@Override
	public void init(String[] args) {
		super.init(args);

		
		currentMode = MODE_GOODS_RECEIPT;
		
		if( args != null && args.length>0 ) {
			String s = args[0];
			if(s.startsWith(MODE_MATERIAL) ) {
				currentMode = MODE_MATERIAL; 
			}
		}
		
	}

	private void reset() {
		resetPos();
		currentAmountOfProcessedUnitLoads = 0;
		currentGoodsReceipt = null;
			
		selectedGoodsReceiptId = null;
		goodsReceiptList = null;
		adviceList = null;

	}
	
	private void resetPos() {
		currentFixTarget = null;
		currentLot = null;
		currentLotDate = null;
		currentUlLabel = null;
		currentUlLabelSelected = false;
		currentAmount = BigDecimal.ZERO;
		currentSerialNo = null;
		currentAdvice = null;
		currentItemData = null;
		currentUnitLoadType = null;
		currentAddLocation = null;
		
		inputAmount = null;
		inputLotName = null;
		
		selectedAdviceId = null;
		unitLoadTypeMap = null;

		inputCode = null;
	}
	
	private void initOrder() {
		resetPos();
		currentAmountOfProcessedUnitLoads = 0;
		adviceList = null;
	}
	
	private void initPos() {
		log.debug("Initialize position");
		currentFixTarget = null;
		currentLot = null;
		currentLotDate = null;
		currentUlLabel = null;
		currentUlLabelSelected = false;
		currentAmount = BigDecimal.ZERO;
		currentSerialNo = null;
		currentUnitLoadType = null;
		currentAddLocation = null;

		unitLoadTypeMap = null;

		inputCode = null;
		inputAmount = null;
		inputLotName = null;
		
		if( currentAdvice != null ) {
			currentItemData = currentAdvice.getItemData();
		}
		if( currentItemData == null ) {
			return;
		}

		currentAmount.setScale(currentItemData.getScale());
		List<LOSFixedLocationAssignment> fixList = fixService.getByItemData(currentItemData);
		if( fixList != null && fixList.size()>0 ) {
			log.debug("Check fixed location...");
			LOSFixedLocationAssignment fix = fixList.get(0);
			currentFixTarget = fix.getAssignedLocation();
			
			// Check the location
			List<StockUnit> stockList = queryStockService.getListByStorageLocation(currentFixTarget);
			if( stockList.size()>1 ) {
				log.info("More than one stock unit on fixed location. Do not use");
				currentFixTarget = null;
			}
			else if( stockList.size() == 1 ) {
				if( !stockList.get(0).getItemData().equals(currentItemData) ) {
					log.info("Wrong item data on fixed location. Do not use");
					currentFixTarget = null;
				}
			}
		}
		
		currentUnitLoadType = currentItemData.getDefaultUnitLoadType();
		if( currentUnitLoadType == null ) {
			currentUnitLoadType = queryUltService.getDefaultUnitLoadType();
		}

	}
	
	private void initUl() {
		currentUlLabel = null;
		currentUlLabelSelected = false;
		currentSerialNo = null;
		inputCode = null;
	}
	
	
	
	
	
	
	
	
	// ***********************************************************************
	// chooseOrder.jsp
	// ***********************************************************************
	public String processChooseOrder(){
		currentMode = MODE_GOODS_RECEIPT;
		
		goodsReceiptList = null;
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";
		
		if( code.length() > 0 ) {
			
			List<GoodsReceiptTO> grList;
			grList = queryGoodsReceiptService.getOpenDtoListByCode(code, limitAmountToNotified, LOSGoodsReceiptState.RAW, LOSGoodsReceiptState.ACCEPTED);
			if( grList.size() == 0 ) {
				JSFHelper.getInstance().message( resolve("MsgNothingFound") );
				return "";
			}
			goodsReceiptList = new ArrayList<SelectItem>();
			if( grList.size() != 1 ) {
				for(GoodsReceiptTO gr:grList){
					String label = gr.getGoodsReceiptNo()+" / "+gr.getSuplier()+" / "+gr.getDeliveryNoteNo();
					goodsReceiptList.add(new SelectItem(gr.getId(), label));
				}
				return"";
			}
			selectedGoodsReceiptId = grList.get(0).getId();
			goodsReceiptList = null;
		}

		if( selectedGoodsReceiptId == null ) {
			JSFHelper.getInstance().message( resolve("MsgSelectGR") );
			return "";
		}
		try {
			currentGoodsReceipt = queryGoodsReceiptService.fetchEager(selectedGoodsReceiptId);
			
			if(currentGoodsReceipt.getAssignedAdvices().size() == 0){
				JSFHelper.getInstance().message( resolve("MsgNoAdviceAssigned") );
				return "";
			}
			
		} catch (EntityNotFoundException e) {
			JSFHelper.getInstance().message( resolve("MsgGRDeleted") );
			return "";
		} catch (UnAuthorizedException e) {
			JSFHelper.getInstance().message( resolve("MsgNotAuthorized") );
			return "";
		}
		
		initOrder();

		// Try to identify the advice with the given code
		if( code.length() > 0 ) {
			List<LOSAdvice> adList;
			adList = queryAdviceService.getListByGoodsReceipCode( currentGoodsReceipt, code, limitAmountToNotified );
			if( adList.size() == 1 ) {
				selectedAdviceId = adList.get(0).getId();
			}
		}

		return GRDirectNavigationEnum.GRD_CHOOSE_POS.name();
	}

	public String processChooseOrderCancel(){
		reset();
		return GRDirectNavigationEnum.GRD_BACK_TO_MENU.name();
	}

	public List<SelectItem> getGoodsReceiptList(){
		log.info("getGoodsReceiptList");

		if( goodsReceiptList == null ) {
			log.info("Read new Map");
			goodsReceiptList = new ArrayList<SelectItem>();
		
			List<GoodsReceiptTO> grList;
			grList = queryGoodsReceiptService.getDtoListByStates(LOSGoodsReceiptState.RAW, LOSGoodsReceiptState.ACCEPTED);
			
			for(GoodsReceiptTO gr:grList){
				String label = gr.getGoodsReceiptNo()+" / "+gr.getSuplier()+" / "+gr.getDeliveryNoteNo();
				goodsReceiptList.add(new SelectItem(gr.getId(), label));
			}
		}

		return goodsReceiptList;
	}
	
	public void setSelectedGoodsReceipt(String sel){
		selectedGoodsReceiptId = null;
		try{
			selectedGoodsReceiptId = Long.valueOf(sel);
		}
		catch( Throwable t ) {}
	}
	
	public String getSelectedGoodsReceipt(){
		return selectedGoodsReceiptId == null ? null : selectedGoodsReceiptId.toString();
	}

	
	
	
	// ***********************************************************************
	// ChoosePos.jsp
	// ***********************************************************************
	public String processChoosePos(){
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";
		Long lastAdviceId = currentAdvice == null ? null : currentAdvice.getId();
		currentAdvice = null;
		currentItemData = null;
		
		if( code.length() > 0 ) {
			
			List<LOSAdvice> adList;
			adList = queryAdviceService.getListByGoodsReceipCode( currentGoodsReceipt, code, limitAmountToNotified );
			if( adList.size() == 0 ) {
				JSFHelper.getInstance().message( resolve("MsgNothingFound") );
				return "";
			}
			adviceList = new ArrayList<SelectItem>();
			if( adList.size() != 1 ) {
				for(LOSAdvice ad:adList){
					String label = ad.getItemData().getNumber()+" / "+(ad.getLot() == null ? " --- / " : ad.getLot().getName()+" / ")+ad.getNotifiedAmount();
					adviceList.add(new SelectItem(ad.getId(), label));
				}
				return"";
			}
			selectedAdviceId = adList.get(0).getId();
			adviceList = null;
		}
		
		if( selectedAdviceId == null ) {
			selectedAdviceId = lastAdviceId;
		}
		if( selectedAdviceId == null ) {
			JSFHelper.getInstance().message( resolve("MsgSelectAdvice") );
			return "";
		}
		
		try {
			currentAdvice = queryAdviceService.getById(selectedAdviceId);
			currentItemData = currentAdvice.getItemData();
		} catch (Exception e) {
			log.error("Cannot find advice with id="+selectedAdviceId);
			JSFHelper.getInstance().message( resolve("MsgSelectAdvice") );
			return "";
		}

		
		if( lastAdviceId != null && lastAdviceId.equals(selectedAdviceId) ) {
			initUl();
		}
		else {
			initPos();
		}

		ItemData itemData = currentAdvice.getItemData();
		if( collectLotAlways || itemData.isLotMandatory() ) {
			return GRDirectNavigationEnum.GRD_ENTER_LOT.name();
		}
		else if( itemData.getSerialNoRecordType() == SerialNoRecordType.ALWAYS_RECORD ) {
			return GRDirectNavigationEnum.GRD_ENTER_SERIAL.name();
		}
		
		return GRDirectNavigationEnum.GRD_ENTER_AMOUNT.name();
	}
	
	public String processChoosePosCancel(){
		resetPos();
		return GRDirectNavigationEnum.GRD_CHOOSE_GR.name();
	}

	public List<SelectItem> getAssignedAdviceList(){
		
		if( adviceList == null ) {
			adviceList = new ArrayList<SelectItem>();
		
			if( currentGoodsReceipt == null ) {
				return adviceList;
			}
			
			List<LOSAdvice> adList = currentGoodsReceipt.getAssignedAdvices();
		
			for(LOSAdvice ad:adList){
				if( limitAmountToNotified ) {
					if( ad.getNotifiedAmount().compareTo(ad.getReceiptAmount()) <= 0 ) {
						continue;
					}
				}
				String label = ad.getItemData().getNumber()+" / "+(ad.getLot() == null ? " --- / " : ad.getLot().getName()+" / ")+ad.getNotifiedAmount();
				adviceList.add(new SelectItem(ad.getId(), label));
			}
		}
		
		return adviceList;
	}
	
	public void setSelectedAdvice(String sel){
		selectedAdviceId = null;
		try{
			selectedAdviceId = Long.valueOf(sel);
		}
		catch( Throwable t ) {}
	}
	
	public String getSelectedAdvice(){
		return selectedAdviceId == null ? null : selectedAdviceId.toString();
	}

	
	// ***********************************************************************
	// EnterMat.jsp
	// ***********************************************************************
	public String processEnterMat() {
		currentMode = MODE_MATERIAL;

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
		initPos();
		
		if( collectLotAlways || mat.isLotMandatory() ) {
			return GRDirectNavigationEnum.GRD_ENTER_LOT.name();
		}
		else if( mat.getSerialNoRecordType() == SerialNoRecordType.ALWAYS_RECORD ) {
			return GRDirectNavigationEnum.GRD_ENTER_SERIAL.name();
		}
		
		return GRDirectNavigationEnum.GRD_ENTER_AMOUNT.name();
	}
	
	public String processEnterMatCancel() {
		reset();
		return GRDirectNavigationEnum.GRD_BACK_TO_MENU.name();
	}
	
	// ***********************************************************************
	// EnterLot.jsp / EnterLotDate.jsp
	// ***********************************************************************
	public String processEnterLot() {
		inputLotName = inputLotName == null ? "" : inputLotName.trim();

		if( inputLotName == null || inputLotName.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterLot") );
			return "";
		}
		
		if( currentItemData == null ) {
			log.fatal("Cannot find selected material! => Abort dialog");
			return GRDirectNavigationEnum.GRD_BACK_TO_MENU.name();
		}
		
		currentLot = queryLotService.getByNameAndItemData(inputLotName, currentItemData);
		if( currentLot == null ) {
			return GRDirectNavigationEnum.GRD_ENTER_LOTDATE.name();
		}
		
		// Check if the valid period of received goods is long enough
		if(currentItemData.getResidualTermOfUsageGI() > 0){
			if( currentLot.getBestBeforeEnd() != null) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, currentItemData.getResidualTermOfUsageGI());
				Date minimumValidTo = cal.getTime();
				
				if(minimumValidTo.after(currentLot.getBestBeforeEnd())){
					JSFHelper.getInstance().message( resolve("MsgLotNotValid", ""+currentItemData.getResidualTermOfUsageGI() ) );
					return "";
				}
			}
		}

		// Check the stock on the fixed target location
		if( currentFixTarget != null ) {
			List<StockUnit> stockList = queryStockService.getListByStorageLocation(currentFixTarget);
			if( stockList.size()>1 ) {
				log.info("More than one stock unit on fixed location. Do not use");
				currentFixTarget = null;
			}
			else if( stockList.size() == 1 ) {
				if( !stockList.get(0).getItemData().equals(currentItemData) ) {
					log.info("Wrong item data on fixed location. Do not use");
					currentFixTarget = null;
				}
				if( !stockList.get(0).getLot().getName().equals(inputLotName) ) {
					log.info("Wrong lot on fixed location. Do not use");
					currentFixTarget = null;
				}
			}


		}
		if( currentItemData.getSerialNoRecordType() == SerialNoRecordType.ALWAYS_RECORD ) {
			return GRDirectNavigationEnum.GRD_ENTER_SERIAL.name();
		}

		return GRDirectNavigationEnum.GRD_ENTER_AMOUNT.name();
	}
	
	public String processEnterLotCancel() {
		initPos();
		return getStartPage2();
	}
	
	public String processEnterLotDate() {
		String code = inputCode == null ? "" : inputCode.trim();

		if( currentItemData == null ) {
			log.fatal("Cannot find selected material! => Abort dialog");
			return GRDirectNavigationEnum.GRD_BACK_TO_MENU.name();
		}
		
		if(code.length() == 0){
			if(currentItemData.getResidualTermOfUsageGI() > 0){
				JSFHelper.getInstance().message( resolve("MsgEnterLotDate") );
				return "";
			}
		}
		else{
			currentLotDate = DateHelper.parseInputString(code);
			if( currentLotDate == null ) {
				JSFHelper.getInstance().message( resolve("MsgEnterLotDate") );
				return "";
			}
		}
			
		// Check if the valid period of received goods is long enough
		if(currentItemData.getResidualTermOfUsageGI() > 0){
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, currentItemData.getResidualTermOfUsageGI());
			Date minimumValidTo = cal.getTime();
			
			if(minimumValidTo.after(currentLotDate)){
				JSFHelper.getInstance().message( resolve("MsgLotNotValid", ""+currentItemData.getResidualTermOfUsageGI() ) ); 
				return "";
			}
		}


		if( inputLotName == null || inputLotName.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterLot") );
			return GRDirectNavigationEnum.GRD_ENTER_LOT.name();
		}

		currentLot = goodsReceiptFacade.createLot(
				currentItemData.getClient(), 
				inputLotName, 
				currentItemData, 
				currentLotDate);
		
		inputCode = null;
		
		if( currentItemData.getSerialNoRecordType() == SerialNoRecordType.ALWAYS_RECORD ) {
			return GRDirectNavigationEnum.GRD_ENTER_SERIAL.name();
		}

		return GRDirectNavigationEnum.GRD_ENTER_AMOUNT.name();
	}
	public String processEnterLotDateCancel() {
		return GRDirectNavigationEnum.GRD_ENTER_LOT.name();
	}
	
	

	
	// ***********************************************************************
	// EnterSerial.jsp
	// ***********************************************************************
	public String processEnterSerial() {
		String code = inputCode == null ? "" : inputCode.trim();

		if( code.length() < 1 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterSerial") );
			return "";
		}
		
		if( currentItemData == null ) {
			log.fatal("Cannot find selected material! => Abort dialog");
			return GRDirectNavigationEnum.GRD_BACK_TO_MENU.name();
		}

		if( ! goodsReceiptFacade.checkSerialNumber(code, currentItemData) ) {
			JSFHelper.getInstance().message( resolve("MsgSerialNotValid") );
			return "";
		}
		
		
		inputCode = "";
		currentAmount = BigDecimal.ONE;
		currentSerialNo = code;
		
		if( ! ItemUnitType.PIECE.equals(currentItemData.getHandlingUnit().getUnitType()) ) {
			return GRDirectNavigationEnum.GRD_ENTER_AMOUNT.name();
		}
		else if( currentFixTarget == null && collectUlType ) {
			return GRDirectNavigationEnum.GRD_ENTER_UL_TYPE.name();
		}
		else if( currentFixTarget == null && collectUlNo ) {
			return GRDirectNavigationEnum.GRD_ENTER_UL_NO.name();
		}

		return GRDirectNavigationEnum.GRD_ENTER_TARGET_LOC.name();
	}
	
	public String processEnterSerialCancel() {
		initPos();
		return getStartPage2();
	}
	

	
	
	// ***********************************************************************
	// EnterAmount.jsp / ConfirmAmount.jsp
	// ***********************************************************************
	public String processEnterAmount() {
		inputAmount = inputAmount == null ? "" : inputAmount.trim();
		
		if( inputAmount.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterAmount") );
			currentAmount = BigDecimal.ZERO;
			return "";
		}
		try {
			currentAmount = new BigDecimal(inputAmount);
		}
		catch( Throwable t ) {
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

		if( currentAdvice != null ) {
			BigDecimal amount = currentAdvice.getNotifiedAmount().subtract(currentAmount).subtract(currentAdvice.getReceiptAmount());
			if( BigDecimal.ZERO.compareTo(amount) > 0 ) {
				if( limitAmountToNotified ) {
					log.error("Amount is limited to notifiedAmount="+currentAdvice.getNotifiedAmount()+". receiptAmount="+currentAdvice.getReceiptAmount()+", enteredAmount="+currentAmount);
					JSFHelper.getInstance().message( resolve("MsgAmountLimited") );
					currentAmount = BigDecimal.ZERO;
					return "";
				}
				return GRDirectNavigationEnum.GRD_CONFIRM_AMOUNT.name();
			}
		}
		
		if( currentFixTarget == null && collectUlType ) {
			return GRDirectNavigationEnum.GRD_ENTER_UL_TYPE.name();
		}
		else if( currentFixTarget == null && collectUlNo ) {
			return GRDirectNavigationEnum.GRD_ENTER_UL_NO.name();
		}

		return GRDirectNavigationEnum.GRD_ENTER_TARGET_LOC.name();
	}
	public String processEnterAmountCancel() {
		initPos();
		return getStartPage2();
	}

	public String processConfirmAmount() {
		if( currentFixTarget == null && collectUlType ) {
			return GRDirectNavigationEnum.GRD_ENTER_UL_TYPE.name();
		}
		else if( currentFixTarget == null && collectUlNo ) {
			return GRDirectNavigationEnum.GRD_ENTER_UL_NO.name();
		}

		return GRDirectNavigationEnum.GRD_ENTER_TARGET_LOC.name();
	}
	public String processConfirmAmountCancel() {
		return GRDirectNavigationEnum.GRD_ENTER_AMOUNT.name();
	}

	
	
	
	// ***********************************************************************
	// EnterUl*.jsp
	// ***********************************************************************
	public String processEnterUlNo() {
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";

		LOSUnitLoad ul = null;
		try {
			ul = queryUlService.getByLabelId(code);
		} catch (UnAuthorizedException e) {
			JSFHelper.getInstance().message( resolve("MsgUlAlreadyUsed") );
			return "";
		}
		if( ul != null ) {
			JSFHelper.getInstance().message( resolve("MsgUlAlreadyUsed") );
			return "";
		}
		if( ! generateUlNo && code.length()==0 ) {
			JSFHelper.getInstance().message( resolve("MsgUlMandatory") );
			return "";
		}
		
		currentUlLabel = code.length()>0 ? code : null;
		currentUlLabelSelected = true;
		
		return GRDirectNavigationEnum.GRD_ENTER_TARGET_LOC.name();
	}
	public String processEnterUlNoCancel() {
		initPos();
		return getStartPage2();
	}
	
	public String processEnterUlNoAdd() {
		// remove the marker to add to fixed location
		currentFixTarget = null;
		inputCode = "";

		return GRDirectNavigationEnum.GRD_ENTER_TARGET_UL.name();
	}

	public String processEnterUlType() {
		if( currentUnitLoadType == null ) {
			JSFHelper.getInstance().message( resolve("MsgEnterUlType") );
			return "";
		}

		if( currentFixTarget == null && collectUlNo ) {
			return GRDirectNavigationEnum.GRD_ENTER_UL_NO.name();
		}

		return GRDirectNavigationEnum.GRD_ENTER_TARGET_LOC.name();
	}
	public String processEnterUlTypeCancel() {
		initPos();
		return getStartPage2();
	}
	
	public String processEnterUlTypeAdd() {
		// remove the marker to add to fixed location
		currentFixTarget = null;
		inputCode = "";

		return GRDirectNavigationEnum.GRD_ENTER_TARGET_UL.name();
	}

	public List<SelectItem> getUnitLoadTypeList(){
		
		unitLoadTypeMap = new HashMap<String, UnitLoadType>();
		
		List<UnitLoadType> ultList = queryUltService.getSortedList(true, false, false, false, false);
		
		List<SelectItem> selList = new ArrayList<SelectItem>(ultList.size());
		
		for(UnitLoadType ult:ultList){
			
			unitLoadTypeMap.put(ult.getName(), ult);
			selList.add(new SelectItem(ult.getName()));
		}
		
		return selList;
	}
	
	public void setSelectedUnitLoadType(String sel){
		
		currentUnitLoadType = unitLoadTypeMap.get(sel);
	}
	
	public String getSelectedUnitLoadType(){
		if(currentUnitLoadType != null){
			return currentUnitLoadType.getName();
		}
		else{
			return null;
		}
	}

	
	
	// ***********************************************************************
	// EnterTargetLoc.jsp
	// ***********************************************************************
	public String processEnterTargetLoc() {
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";
		
		if( code.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterLoc") );
			return "";
		}
		
		if( currentFixTarget != null ) {
			// The mode is to store to the fixed location
			// It is alternatively possible to put the material to an already existing stock
			if( currentFixTarget.getName().equals(code) ) {
				return postUnitLoad( currentFixTarget.getName(), null );
			}

			LOSUnitLoad targetUl = null;
			LOSStorageLocation loc = null;
			try {
				loc = locService.getByName(code);
			}
			catch( Exception e ) {
				log.error("Cannot select location="+code+". ex="+e.getMessage(), e);
			}
			if( loc == null ) {
				log.error("Wrong location entered. location="+code);
				JSFHelper.getInstance().message( resolve("MsgLocNotAccessable") );
				return "";
			}

			StockUnit su = null;
			List<StockUnit> stockList = queryStockService.getListByStorageLocation(loc);
			for( StockUnit su1 : stockList ) {
				if( su1.getItemData().equals(currentItemData) ) {
					su = su1;
					try {
						targetUl = (LOSUnitLoad) queryUnitLoadRemote.queryById(su.getUnitLoad().getId());
					} catch (Exception e) {
					}
					if( targetUl != null && targetUl.getStockUnitList().size() == 1 ) {
						break;
					}
					log.info("More than one stock on UnitLoad="+targetUl.getLabelId());
					targetUl = null;
				}
			}
			if( targetUl != null ) {
				return postUnitLoad( null, targetUl.getLabelId() );
			}
			
			JSFHelper.getInstance().message( resolve("MsgCannotReadStock") );
			return "";
		}
		
		
		// The mode is to store not to the fixed location

		LOSStorageLocation loc = null;
		try {
			loc = locService.getByName(code);
		}
		catch( Exception e ) {
			log.error("Cannot select location="+code+". ex="+e.getMessage(), e);
		}
		if( loc == null ) {
			log.error("Wrong location entered. location="+code);
			JSFHelper.getInstance().message( resolve("MsgLocNotAccessable") );
			return "";
		}

		return postUnitLoad( loc.getName(), null );
		
	}

	public String processEnterTargetUl() {
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";
		
		if( code.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterUl") );
			return "";
		}

		LOSUnitLoad targetUl = null;
		LOSUnitLoad ul = null;
		LOSStorageLocation loc = null;
		List<StockUnit> stockList;
		
		try {
			ul = queryUlService.getByLabelId(code);
		} catch (UnAuthorizedException e1) {
			log.error("Unit load not accessable: "+code);
			JSFHelper.getInstance().message( resolve("MsgUlNotAccessable") );
			return "";
		}
		
		if( ul != null ) {
			stockList = queryStockService.getListByUnitLoad(ul);
		}
		else {
			try {
				loc = locService.getByName(code);
			} catch (Exception e) {
			}
			if( loc == null ) {
				log.error("No Unit load or location found for code="+code);
				JSFHelper.getInstance().message( resolve("MsgUlNotFound") );
				return "";
			}

			stockList = queryStockService.getListByStorageLocation(loc);
		}
		
		for( StockUnit su : stockList ) {
			if( su.getItemData().equals(currentItemData) ) {
				try {
					ul = (LOSUnitLoad) queryUnitLoadRemote.queryById(su.getUnitLoad().getId());
				} catch (Exception e) {
				}
				if( ul != null && ul.getStockUnitList().size() == 1 ) {
					targetUl = ul;
					break;
				}
				log.info("More than one stock on UnitLoad="+ul.getLabelId());
			}
		}
		if( targetUl != null ) {
			return postUnitLoad( null, targetUl.getLabelId() );
		}
		
		log.error("Cannot use unit load="+code);
		JSFHelper.getInstance().message( resolve("MsgUlNotAccessable") );
		return "";
	}
	
	public String processEnterTargetCancel() {
		initPos();
		return getStartPage2();
	}

	public String processEnterTargetStore() {
		// remove the marker to add to fixed location
		currentFixTarget = null;
		inputCode = "";

		if( currentUlLabelSelected ) {
			return GRDirectNavigationEnum.GRD_ENTER_TARGET_LOC.name();
		}
		
		if( collectUlType ) {
			return GRDirectNavigationEnum.GRD_ENTER_UL_TYPE.name();
		}
		else if( collectUlNo ) {
			return GRDirectNavigationEnum.GRD_ENTER_UL_NO.name();
		}

		return GRDirectNavigationEnum.GRD_ENTER_TARGET_LOC.name();
	}

	public String processEnterTargetAdd() {
		// remove the marker to add to fixed location
		currentFixTarget = null;
		inputCode = "";

		return GRDirectNavigationEnum.GRD_ENTER_TARGET_UL.name();
	}
	
	public boolean isTargetHasStoreOption() {
		return !currentUlLabelSelected;
	}
	
	public boolean isTargetHasAddOption() {
		return true;
	}
	
	// ***********************************************************************
	// Posting
	// ***********************************************************************
	public String postUnitLoad( String targetLocName, String targetUlName ){
		String logStr = "postUnitLoad ";
		
		if( currentAmount == null || BigDecimal.ZERO.compareTo(currentAmount) >= 0 ) {
			log.error(logStr+"Amount not valid. amount="+currentAmount);
			JSFHelper.getInstance().message( resolve("MsgAmountNotValid") );
			return GRDirectNavigationEnum.GRD_ENTER_AMOUNT.name();
		}
		
		if( currentUnitLoadType == null ) {
			currentUnitLoadType = queryUltService.getDefaultUnitLoadType();
		}

		if( currentMode == MODE_MATERIAL ) {
			return postMaterial(targetLocName, targetUlName);
		}
		else {
			return postAdvice(targetLocName, targetUlName);
		}
	}
	
	public String postMaterial( String targetLocName, String targetUlName ){
		String logStr = "postMaterial ";
		
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
										currentLot == null ? null : currentLot.getName(), 
										currentItemData.getNumber(),
										currentUlLabel,
										currentUnitLoadType.getName(),
										currentAmount,
										lock, null, currentSerialNo, targetLocName, targetUlName );
			
			
			currentAmountOfProcessedUnitLoads++;
			
			
		} catch ( FacadeException e) {
			log.error(logStr+"Error in posting ("+getLocale()+"): "+e.getMessage());
			String msg = ((FacadeException) e).getLocalizedMessage( getLocale() );
			JSFHelper.getInstance().message(msg);
			return "";
			
		} catch ( Throwable e) {
			log.error(logStr+"General Error in posting: "+e.getMessage(), e);
			String msg = e.getLocalizedMessage();
			JSFHelper.getInstance().message(msg);
			return "";
		}
		
		return GRDirectNavigationEnum.GRD_ENTER_MAT.name();
	}
	
	public String postAdvice( String targetLocName, String targetUlName ){
		String logStr = "postAdvice ";
		if( currentAdvice == null ) {
			log.error(logStr+"Advice not loaded");
			JSFHelper.getInstance().message( resolve("MsgAdviceNotLoaded") );
			return GRDirectNavigationEnum.GRD_CHOOSE_POS.name();
		}
		
		ItemData item = currentAdvice.getItemData();
		int lock = (int)propertyService.getLongDefault(getWorkstationName(), "GOODS_IN_DEFAULT_LOCK", 0);
		Client cl = currentGoodsReceipt.getClient();
		
		String printer = propertyService.getStringDefault(null, getWorkstationName(), LOSGoodsReceiptComponent.PROPERTY_KEY_PRINTER_GR, ReportService.NO_PRINTER);

		try {
			BODTO<Lot> lotTo = null;
			if( currentLot != null ) {
				lotTo = new BODTO<Lot>(currentLot.getId(), currentLot.getVersion(), currentLot.getName());
			}

			goodsReceiptFacade.createGoodsReceiptPosition(
										new BODTO<Client>(cl.getId(), cl.getVersion(), cl.getNumber()), 
										currentGoodsReceipt, 
										lotTo, 
										new BODTO<ItemData>(item.getId(), item.getVersion(), item.getNumber()),
										currentUlLabel,
										new BODTO<UnitLoadType>(currentUnitLoadType.getId(), currentUnitLoadType.getVersion(), currentUnitLoadType.getName()),
										currentAmount,
										new BODTO<LOSAdvice>(currentAdvice.getId(), currentAdvice.getVersion(), currentAdvice.getAdviceNumber()),
										LOSGoodsReceiptType.INTAKE,
										lock, null, currentSerialNo, targetLocName, targetUlName,
										printer );
			
			
			currentAmountOfProcessedUnitLoads++;
			
			
		} catch ( Throwable e) {

			String msg;
			if( e instanceof FacadeException ) {
				log.error(logStr+"Error in posting: "+e.getMessage());
				msg = ((FacadeException) e).getLocalizedMessage( getLocale() );
			}
			else {
				log.error(logStr+"Error in posting: "+e.getMessage(), e);
				msg = e.getLocalizedMessage();
			}
			JSFHelper.getInstance().message(msg);
			return "";
		}
		
		return GRDirectNavigationEnum.GRD_CHOOSE_POS.name();
	}

	
	
	
	// ***********************************************************************
	// Attributes
	// ***********************************************************************
	public String getlot() {
		return currentLot == null ? "" : currentLot.getName();
	}
	
	public boolean isShowLot() {
		return currentLot != null;
	}
	public boolean isShowOrder() {
		return currentAdvice != null;
	}
	public String getAdviceLot() {
		return currentAdvice == null ? null : currentAdvice.getLot() == null ? null : currentAdvice.getLot().getName();
	}
	
	public String getInputCode() {
		return inputCode;
	}
	public void setInputCode(String inputCode) {
		this.inputCode = inputCode;
	}
	public String getOrderNo() {
		return currentGoodsReceipt == null ? "" : currentGoodsReceipt.getGoodsReceiptNumber();
	}

	public String getItemDataNumber() {
		return currentItemData == null ? "" : currentItemData.getNumber();
	}
	
	public String getItemDataName() {
		return currentItemData == null ? "" : currentItemData.getName();
	}
	
	public String getAmountPos() {
		if( currentAdvice == null ) {
			return "";
		}
		return "" + currentAdvice.getReceiptAmount() + " / " + currentAdvice.getNotifiedAmount() + " "+ currentAdvice.getItemData().getHandlingUnit();
	}

	public String getAmount() {
		return currentAmount == null ? "" : currentAmount.toString() + " "+ getCurrentUnit();
	}
	
	public String getTargetLocation() {
		if( currentFixTarget != null ) {
			return currentFixTarget.getName();
		}
		return "";
	}
	public boolean isHasTargetLocation() {
		return currentFixTarget != null;
	}
	public String getInputAmount() {
		return inputAmount;
	}
	public void setInputAmount(String inputAmount) {
		this.inputAmount = inputAmount;
	}
	
	public String getInputLotName() {
		return inputLotName;
	}
	public void setInputLotName(String inputLotName) {
		this.inputLotName = inputLotName;
	}
	
	public String getCurrentUnit() {
		if( currentItemData == null ) {
			return "";
		}
		return currentItemData.getHandlingUnit().getUnitName();
	}

	public String getTargetAddLocation() {
		if( currentAddLocation != null ) {
			return currentAddLocation;
		}
		try {
			currentAddLocation = goodsReceiptFacade.getOldestLocationToAdd(currentItemData, currentLot);
		} catch (Exception e) { 
			log.error("Exception. Cannot find Target to add: "+e.getMessage(), e );
		}
		
		return currentAddLocation;
	}
	
	public String getDeliverer() {
		return currentGoodsReceipt == null ? "" : currentGoodsReceipt.getForwarder();
	}
	public String getDeliveryNote() {
		return currentGoodsReceipt == null ? "" : currentGoodsReceipt.getDeliveryNoteNumber();
	}
	public String getReceiptDate() {
		if( currentGoodsReceipt == null ) {
			return "";
		}
		
		return DateFormat.getDateInstance(DateFormat.SHORT, super.getLocale()).format(currentGoodsReceipt.getReceiptDate());

	}
	public boolean isCurrentUlLabelSelected() {
		return currentUlLabelSelected;
	}
	
	public String getCurrentUlLabel() {
		return currentUlLabel == null ? resolve("LabelGenerated") : currentUlLabel;
	}
	
	
	public String getStartPage1() {
		if( currentMode == MODE_MATERIAL ) {
			return GRDirectNavigationEnum.GRD_ENTER_MAT.name();
		}
		return GRDirectNavigationEnum.GRD_CHOOSE_GR.name();
	}
	public String getStartPage2() {
		if( currentMode == MODE_MATERIAL ) {
			return GRDirectNavigationEnum.GRD_ENTER_MAT.name();
		}
		return GRDirectNavigationEnum.GRD_CHOOSE_POS.name();
	}
	
	// ***********************************************************************
	@Override
	protected ResourceBundle getResourceBundle() {
		ResourceBundle bundle;
		Locale loc;
		loc = getUIViewRoot().getLocale();
		bundle = ResourceBundle.getBundle("de.linogistix.mobile.processes.gr_direct.GRDirectBundle", loc);
		return bundle;
	}

}

package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;
import java.util.Date;

import org.mywms.model.VehicleData;

import de.linogistix.los.inventory.model.LOSFuelOrderLog;
import de.linogistix.los.query.BODTO;

import org.apache.log4j.Logger;
//import java.util.ResourceBundle;
public class LOSFuelOrderLogTO extends BODTO<LOSFuelOrderLog> {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(LOSFuelOrderLogTO.class);

/*	
	private String vehicleData;
	private String storageLocation;	
	private int stationPump;
	private String orderReceipient;
	private long orderRcptPos;
	private String orderType;
	private BigDecimal tankRemaining;

	private String plateNumber;
	private String fullName;
	private String firstName;
	private String rankName;
	private String lastName;
	private String articleRef;
	private String articleDescr;
*/
	private Date transactionDate;
	private long vehicleID;	
    private String vehicleLabelID;
    private String vehiclePlateNumber;
    private long storLocID;
    private String storLocName;
    private int stationPump;
    private long receipientID;
    private String receipientName;
    private String receipientTokenID;
    private String receipientIDCard;
    private long rcptPosID;
    private String rcptArticleDescr;
    private String rcptArticleRef; 
    private BigDecimal rcptPosQuantity;
    private String orderType;
    private BigDecimal tankRemaining;
	
	public LOSFuelOrderLogTO(LOSFuelOrderLog idat) {
        	super(idat.getId(), idat.getVersion(), idat.getTransactionId());
        this.transactionDate = idat.getCreated();
        this.vehicleID = idat.getVehicleID();
        this.vehicleLabelID = idat.getVehicleLabelID();
        this.vehiclePlateNumber = idat.getVehiclePlateNumber();
		this.storLocID = idat.getStorLocID();
        this.storLocName = idat.getStorLocName();		
		this.stationPump	= idat.getStationPump();
		this.receipientID	= idat.getReceipientID();
		this.receipientName = idat.getReceipientName();
		this.receipientTokenID = idat.getReceipientTokenID();
		this.receipientIDCard = idat.getReceipientIDCard();
		this.rcptPosID 	= idat.getRcptPosID();
		this.rcptArticleRef 	= idat.getRcptArticleRef();
		this.rcptArticleDescr 	= idat.getRcptArticleDescr();
		this.rcptPosQuantity = idat.getRcptPosQuantity();
		this.orderType		= idat.getOrderType();
		this.tankRemaining	= idat.getTankRemaining();   				   		
    }

	public LOSFuelOrderLogTO(Long id, int version, String transactionId){
		super(id, version, transactionId);
	}
	
	public LOSFuelOrderLogTO(Long id, int version, String transactionId, Date transactionDate, 
			String vehicleLabelID, String vehiclePlateNumber,String storLocName, int stationPump, 
			String receipientName, String receipientTokenID, String receipientIDCard, String rcptArticleDescr,
			String rcptArticleRef, BigDecimal rcptPosQuantity, String orderType, BigDecimal tankRemaining
		)
	{
		super(id, version, transactionId);
		this.transactionDate = transactionDate;
		this.vehicleLabelID = vehicleLabelID;
		this.vehiclePlateNumber = vehiclePlateNumber;
		this.storLocName = storLocName;
		this.stationPump	= stationPump;
		this.receipientName	= receipientName;
		this.receipientTokenID = receipientTokenID;
		this.receipientIDCard = receipientIDCard;
		this.rcptArticleDescr = rcptArticleDescr;
		this.rcptArticleRef = rcptArticleRef;
		this.rcptPosQuantity = rcptPosQuantity;
		this.orderType		= orderType;
		this.tankRemaining	= tankRemaining;   			
	}

	public LOSFuelOrderLogTO(Long id, int version, String transactionId, Date transactionDate, 
			String storLocName, int stationPump, String orderType, 
			String vehiclePlateNumber, String receipientName, String receipientIDCard,
			String rcptArticleRef, String rcptArticleDescr,	BigDecimal rcptPosQuantity, 
			BigDecimal tankRemaining
		)
	{
		super(id, version, transactionId);
		//this.vehicleLabelID = vehicleLabelID;
		this.transactionDate = transactionDate;
		this.vehiclePlateNumber = vehiclePlateNumber;
		this.storLocName = storLocName;
		this.stationPump	= stationPump;
		this.receipientName	= receipientName;
		//this.receipientTokenID = receipientTokenID;
		this.receipientIDCard = receipientIDCard;
		this.rcptArticleDescr = rcptArticleDescr;
		this.rcptArticleRef = rcptArticleRef;
		this.rcptPosQuantity = rcptPosQuantity;
		this.orderType		= orderType;
		this.tankRemaining	= tankRemaining;   			
	}
	
	/**
	 * Get transactionDate;
	 *
	 * @return transactionDate as Date.
	 */
	public Date getTransactionDate()
	{
	    return transactionDate;
	}
	
	/**
	 * Set transactionDate.
	 *
	 * @param transactionDate the value to set.
	 */
	public void setTransactionDate (Date transactionDate)
	{
	    this.transactionDate = transactionDate;
	}
	
	
	/**
	 * Get vehicleLabelID.
	 *
	 * @return vehicleLabelID as String.
	 */
	public String getVehicleLabelID()
	{
	    return vehicleLabelID;
	}
	
	/**
	 * Set vehicleLabelID.
	 *
	 * @param vehicleLabelID the value to set.
	 */
	public void setVehicleLabelID (String vehicleLabelID)
	{
	    this.vehicleLabelID = vehicleLabelID;
	}
	
	/**
	 * Get vehiclePlateNumber.
	 *
	 * @return vehiclePlateNumber as String.
	 */
	public String getVehiclePlateNumber()
	{
	    return vehiclePlateNumber;
	}
	
	/**
	 * Set vehiclePlateNumber.
	 *
	 * @param vehiclePlateNumber the value to set.
	 */
	public void setVehiclePlateNumber(String vehiclePlateNumber)
	{
	    this.vehiclePlateNumber = vehiclePlateNumber;
	}
	/**
	 * Get storageLocation Name.
	 *
	 * @return storageLocation Name as String.
	 */
	public String getStorLocName()
	{
	    return storLocName;
	}
	
	/**
	 * Set storageLocation Name.
	 *
	 * @param storageLocation the value to set.
	 */
	public void setStorLocName(String storLocName)
	{
	    this.storLocName = storLocName;
	}
	
	/**
	 * Get stationPump.
	 *
	 * @return stationPump as int.
	 */
	public int getStationPump()
	{
	    return stationPump;
	}
	
	/**
	 * Set stationPump.
	 *
	 * @param stationPump the value to set.
	 */
	public void setStationPump(int stationPump)
	{
	    this.stationPump = stationPump;
	}
	
	/**
	 * Get receipientName.
	 *
	 * @return receipientName as String.
	 */
	public String getReceipientName()
	{
	    return receipientName;
	}
	
	/**
	 * Set receipientName.
	 *
	 * @param receipientName the value to set.
	 */
	public void setReceipientName(String receipientName)
	{
	    this.receipientName = receipientName;
	}
	
	/**
	 * Get receipientTokenID.
	 *
	 * @return receipientTokenID.
	 */
	public String getReceipientTokenID()
	{
	    return receipientTokenID;
	}
	
	/**
	 * Set receipientTokenID.
	 *
	 * @param receipientTokenID the value to set.
	 */
	public void setReceipientTokenID(String receipientTokenID)
	{
	    this.receipientTokenID = receipientTokenID;
	}
	
	/**
	 * Get receipientIDCard.
	 *
	 * @return receipientIDCard.
	 */
	public String getReceipientIDCard()
	{
	    return receipientIDCard;
	}
	
	/**
	 * Set receipientIDCard.
	 *
	 * @param IDCard the value to set.
	 */
	public void setReceipientIDCard(String receipientIDCard)
	{
	    this.receipientIDCard = receipientIDCard;
	}
	/**
	 * Get orderType.
	 *
	 * @return orderType as String.
	 */
	public String getOrderType()
	{
	    return orderType;
	}
	
	/**
	 * Set orderType.
	 *
	 * @param orderType the value to set.
	 */
	public void setOrderType(String orderType)
	{
	    this.orderType = orderType;
	}
	
	/**
	 * Get tankRemaining.
	 *
	 * @return tankRemaining as BigDecimal.
	 */
	public BigDecimal getTankRemaining()
	{
	    return tankRemaining;
	}
	
	/**
	 * Set tankRemaining.
	 *
	 * @param tankRemaining the value to set.
	 */
	public void setTankRemaining(BigDecimal tankRemaining)
	{
	    this.tankRemaining = tankRemaining;
	}
	
		
	/**
	 * Get articleRef.
	 *
	 * @return articleRef as String.
	 */
	public String getRcptArticleRef()
	{
	    return rcptArticleRef;
	}
	
	/**
	 * Set articleRef.
	 *
	 * @param articleRef the value to set.
	 */
	public void setRcptArticleRef(String rcptArticleRef)
	{
	    this.rcptArticleRef = rcptArticleRef;
	}
	
	/**
	 * Get articleDescr.
	 *
	 * @return articleDescr as String.
	 */
	public String getRcptArticleDescr()
	{
	    return rcptArticleDescr;
	}
	
	/**
	 * Set articleDescr.
	 *
	 * @param articleDescr the value to set.
	 */
	public void setRcptArticleDescr(String rcptArticleDescr)
	{
	    this.rcptArticleDescr = rcptArticleDescr;
	}
	
	/**
	 * Get rcptPosQuantity.
	 *
	 * @return rcptPosQuantity as String.
	 */
	public BigDecimal getRcptPosQuantity()
	{
	    return rcptPosQuantity;
	}
	
	/**
	 * Set rcptPosQuantity.
	 *
	 * @param rcptPosQuantity the value to set.
	 */
	public void setRcptPosQuantity(BigDecimal rcptPosQuantity)
	{
	    this.rcptPosQuantity = rcptPosQuantity;
	}
		

	//@Override
	//protected ResourceBundle getResourceBundle() {
	//ResourceBundle bundle;
	//Locale lo;
	//lo = getUIViewRoot().getLocale();
	//bundle = ResourceBundle.getBundle("de.linogistix.mobile.processes.fuel.FuelBundle", lo);
	//return bundle;
	//}
}

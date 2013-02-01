package de.linogistix.los.inventory.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.mywms.model.BusinessException;
import org.mywms.model.VehicleData;
import org.mywms.service.ConstraintViolatedException;

import org.mywms.model.BasicEntity;

import de.linogistix.los.location.model.LOSStorageLocation;

import de.linogistix.los.inventory.model.LOSOrderReceipients;
import de.linogistix.los.inventory.model.OrderReceiptPosition;


@Entity
@Table(name = "los_fuel_order_log", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "transactionid"
    })
})
public class LOSFuelOrderLog extends BasicEntity {

    private static final long serialVersionUID = 1L;

    private String transactionId;
 //MELISA
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

    @Column(nullable = false)
    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    //TransactionID not unique?
    @Override
    public String toUniqueString() {
        if (getTransactionId() != null) {
            return getTransactionId();
        } else {
            return getId().toString();
        }
    }
    
    @PreUpdate
    @PrePersist
    public void sanityCheck() throws BusinessException, ConstraintViolatedException {

        if (getId() != null) {
            if (( getTransactionId() == null || getTransactionId().length() == 0 )) {
                setTransactionId(getId().toString());
            } else {
                //ok
            }
        } else {
            throw new RuntimeException("Id cannot be retrieved yet - hence TransactionId cannot be set");
        }

    }

    /**
     * Get StorLocID.
     *
     * @return transactionDate as Date
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * Set transactionDate.
     *
     * @param transactionDate the value to set.
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    /**
     * Get StorLocID.
     *
     * @return storLoc as ID from LOSStorageLocation.
     */
    public long getStorLocID() {
        return storLocID;
    }

    /**
     * Set StorLocID.
     *
     * @param storLoc the value to set.
     */
    public void setStorLocID(long storLocID) {
        this.storLocID = storLocID;
    }

    /**
     * Get StorLocName
     *
     * @return StorLocName as Name from LOSStorageLocation.
     */
    public String getStorLocName() {
        return storLocName;
    }

    /**
     * Set StorLocName.
     *
     * @param StorLocName the value to set.
     */
    public void setStorLocName(String storLocName) {
        this.storLocName = storLocName;
    }
    
    /**
     * Get stationPump.
     *
     * @return stationPump as int.
     */
    public int getStationPump() {
        return stationPump;
    }

    /**
     * Set stationPump.
     *
     * @param stationPump the value to set.
     */
    public void setStationPump(int stationPump) {
        this.stationPump = stationPump;
    }

    /**
     * Get receipientId.
     *
     * @return receipientId as Id from LOSOrderReceipients.
     */
    public long getReceipientID() {
        return receipientID;
    }
    
    /**
     * Set receipientId.
     *
     * @param receipientId the value to set.
     */
    public void setReceipientID(long receipientID) {
        this.receipientID = receipientID;
    }

    /**
     * Get receipientTokenID.
     *
     * @return receipientId as TokenID from LOSOrderReceipients.
     */
    public String getReceipientTokenID() {
        return receipientTokenID;
    }
    
    /**
     * Set receipientLabelID.
     *
     * @param receipientLabelID the value to set.
     */
    public void setReceipientTokenID(String receipientTokenID) {
        this.receipientTokenID = receipientTokenID;
    }
    
    /**
     * Get receipientName.
     *
     * @return receipientId as Name from LOSOrderReceipients.
     */
    public String getReceipientName() {
        return receipientName;
    }
    
    /**
     * Set receipientName.
     *
     * @param receipientName the value to set.
     */
    public void setReceipientName(String receipientName) {
        this.receipientName = receipientName;
    }
    
    /**
     * Get receipientIDCard.
     *
     * @return receipientIDCard as Identity Card from LOSOrderReceipients.
     */
    public String getReceipientIDCard() {
        return receipientIDCard;
    }
    
    /**
     * Set receipientIDCard.
     *
     * @param receipientIDcard the value to set.
     */
    public void setReceipientIDCard(String receipientIDCard) {
        this.receipientIDCard = receipientIDCard;
    }
    
    /**
     * Get orderType.
     *
     * @return orderType as String.
     */
    public String getOrderType() {
        return orderType;
    }

    /**
     * Set orderType.
     *
     * @param orderType the value to set.
     */
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    /**
     * Get tankRemaining.
     *
     * @return tankRemaining as BigDecimal.
     */
    @Column(precision=18, scale=6)
    public BigDecimal getTankRemaining() {
        return tankRemaining;
    }

    /**
     * Set tankRemaining.
     *
     * @param tankRemaining the value to set.
     */
    public void setTankRemaining(BigDecimal tankRemaining) {
        this.tankRemaining = tankRemaining;
    }

    /**
     * Get rcptPosId.
     *
     * @return rcptPosId as ID from OrderReceiptPosition.
     */
    public long getRcptPosID() {
        return rcptPosID;
    }

    /**
     * Set rcptPosId.
     *
     * @param rcptPosId the value to set.
     */
    public void setRcptPosID(long rcptPosID) {
        this.rcptPosID = rcptPosID;
    }
    
    /**
     * Get rcptArticleDescr.
     *
     * @return rcptPosItemDescr as rcptArticleDescr from OrderReceiptPosition.
     */
    public String getRcptArticleDescr() {
        return rcptArticleDescr;
    }

    /**
     * Set rcptPosItemRef.
     *
     * @param rcptPosItemRef the value to set.
     */
    public void setRcptArticleRef(String rcptArticleRef) {
        this.rcptArticleRef = rcptArticleRef;
    }
    
    /**
     * Get rcptArticleRef.
     *
     * @return rcptPosItemRef as rcptArticleRef from OrderReceiptPosition.
     */
    public String getRcptArticleRef() {
        return rcptArticleRef;
    }

    /**
     * Set rcptPosItemDescr.
     *
     * @param rcptPosItemDescr the value to set.
     */
    public void setRcptArticleDescr(String rcptArticleDescr) {
        this.rcptArticleDescr = rcptArticleDescr;
    }
    /**
     * Get rcptPosQuantity.
     *
     * @return rcptPosQuantity as Quantity from OrderReceiptPosition.
     */
    @Column(precision=18, scale=6)
    public BigDecimal getRcptPosQuantity() {
        return rcptPosQuantity;
    }

    /**
     * Set rcptPosQuantity.
     *
     * @param rcptPosQuantity the value to set.
     */
    public void setRcptPosQuantity(BigDecimal rcptPosQuantity) {
        this.rcptPosQuantity = rcptPosQuantity;
    } 
    /**
     * Get vehicleId.
     *
     * @return vehicle_Id as ID from VehicleData.
     */
    public long getVehicleID() {
        return vehicleID;
    }
         
    /**
     * Set vehicleId.
     *
     * @param vehicleId the value to set.
     */
    public void setVehicleID(long vehicleID) {
        this.vehicleID = vehicleID;
    }
    
    /**
     * Get vehiclePlateNumber.
     *
     * @return vehiclePlateNumber as PlateNumber from VehicleData.
     */
    public String getVehiclePlateNumber() {
        return vehiclePlateNumber;
    }
    
     
    /**
     * Set vehiclePlateNumber.
     *
     * @param vehiclePlateNumber the value to set.
     */
    public void setVehiclePlateNumber(String vehiclePlateNumber) {
        this.vehiclePlateNumber = vehiclePlateNumber;
    }
    
    /**
     * Get vehicleLabelID.
     *
     * @return vehicleLabelID as vehicleLabelID from VehicleData.
     */
    public String getVehicleLabelID() {
        return vehicleLabelID;
    }
    
     
    /**
     * Set vehicleLabelID.
     *
     * @param vehicleLabelID the value to set.
     */
    public void setVehicleLabelID(String vehicleLabelID) {
        this.vehicleLabelID = vehicleLabelID;
    }
}

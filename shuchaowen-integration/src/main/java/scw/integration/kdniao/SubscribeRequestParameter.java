package scw.integration.kdniao;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import scw.net.http.ToParameterMap;

/**
 * 物流跟踪 订阅请求参数，无特殊说明都是选填参数
 * 
 * @author shuchaowen
 *
 */
public class SubscribeRequestParameter implements Serializable, ToParameterMap {
	private static final long serialVersionUID = 1L;
	// 用户自定义回调信息
	private String callback;
	// 会员标识(备用字段)
	private String memberId;
	// 仓库标识(备用字段)
	private String wareHouseId;
	// 电子面单客户账号(与快递网点申请)
	private String customerName;
	private String customerPwd;
	private String sendSite;
	private String shipperCode;
	private String logisticCode;
	private String orderCode;
	private String monthCode;
	private Integer payType;
	private String expType;
	private String cost;
	private String otherCost;
	private List<ReceiverOrSender> receiver;
	private List<ReceiverOrSender> sender;
	private Integer isNotice;
	private String startDate;
	private String endDate;
	private Double weight;
	private Integer quantity;
	private Double volume;
	private String remark;
	private int isSendMessage;
	private List<AddService> addService;
	private List<Commodity> commodity;

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getWareHouseId() {
		return wareHouseId;
	}

	public void setWareHouseId(String wareHouseId) {
		this.wareHouseId = wareHouseId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerPwd() {
		return customerPwd;
	}

	public void setCustomerPwd(String customerPwd) {
		this.customerPwd = customerPwd;
	}

	public String getSendSite() {
		return sendSite;
	}

	public void setSendSite(String sendSite) {
		this.sendSite = sendSite;
	}

	public String getShipperCode() {
		return shipperCode;
	}

	public void setShipperCode(String shipperCode) {
		this.shipperCode = shipperCode;
	}

	public String getLogisticCode() {
		return logisticCode;
	}

	public void setLogisticCode(String logisticCode) {
		this.logisticCode = logisticCode;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getMonthCode() {
		return monthCode;
	}

	public void setMonthCode(String monthCode) {
		this.monthCode = monthCode;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public String getExpType() {
		return expType;
	}

	public void setExpType(String expType) {
		this.expType = expType;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getOtherCost() {
		return otherCost;
	}

	public void setOtherCost(String otherCost) {
		this.otherCost = otherCost;
	}

	public List<ReceiverOrSender> getReceiver() {
		return receiver;
	}

	public void setReceiver(List<ReceiverOrSender> receiver) {
		this.receiver = receiver;
	}

	public List<ReceiverOrSender> getSender() {
		return sender;
	}

	public void setSender(List<ReceiverOrSender> sender) {
		this.sender = sender;
	}

	public Integer getIsNotice() {
		return isNotice;
	}

	public void setIsNotice(Integer isNotice) {
		this.isNotice = isNotice;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getIsSendMessage() {
		return isSendMessage;
	}

	public void setIsSendMessage(int isSendMessage) {
		this.isSendMessage = isSendMessage;
	}

	public List<AddService> getAddService() {
		return addService;
	}

	public void setAddService(List<AddService> addService) {
		this.addService = addService;
	}

	public List<Commodity> getCommodity() {
		return commodity;
	}

	public void setCommodity(List<Commodity> commodity) {
		this.commodity = commodity;
	}

	public Map<String, Object> toRequestParameterMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("Callback", getCallback());
		map.put("MemberID", getMemberId());
		map.put("WareHouseID", getWareHouseId());
		map.put("CustomerName", getCustomerName());
		map.put("CustomerPwd", getCustomerPwd());
		map.put("SendSite", getSendSite());
		map.put("ShipperCode", getShipperCode());
		map.put("LogisticCode", getLogisticCode());
		map.put("OrderCode", getOrderCode());
		map.put("MonthCode", getMonthCode());
		map.put("PayType", getPayType());
		map.put("ExpType", getExpType());
		map.put("Cost", getCost());
		map.put("OtherCost", getOtherCost());
		map.put("Receiver", getReceiver());
		map.put("Sender", getSender());
		map.put("IsNotice", getIsNotice());
		map.put("StartDate", getStartDate());
		map.put("EndDate", getEndDate());
		map.put("Weight", getWeight());
		map.put("Quantity", getQuantity());
		map.put("Volume", getVolume());
		map.put("Remark", getRemark());
		map.put("IsSendMessage", getIsSendMessage());
		map.put("AddService", getAddService());
		map.put("Commodity", getCommodity());
		return map;
	}
}

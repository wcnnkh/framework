package scw.utils.express.kdniao.response;

import java.util.List;

import scw.json.JSONObject;

/**
 * 快递鸟即时查询API返回
 * 
 * @author shuchaowen
 *
 */
public final class EbusinessOrderHandleResponse extends KDNiaoResponse {
	private static final long serialVersionUID = 1L;
	// 用户ID
	private String businessId;
	// 订单编号
	private String orderCode;
	// 快递公司编码
	private String shipperCode;
	// 物流运单号
	private String logisticCode;
	// 物流状态：2-在途中,3-签收,4-问题件
	private String state;
	private List<Traces> traces;

	public EbusinessOrderHandleResponse(JSONObject json) {
		super(json);
		this.businessId = json.getString("EBusinessID");
		this.orderCode = json.getString("OrderCode");
		this.shipperCode = json.getString("ShipperCode");
		this.logisticCode = json.getString("LogisticCode");
		this.state = json.getString("state");
		this.traces = Traces.parseTraces(json.getJSONArray("traces"));
	}

	public String getBusinessId() {
		return businessId;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public String getShipperCode() {
		return shipperCode;
	}

	public String getLogisticCode() {
		return logisticCode;
	}

	public String getState() {
		return state;
	}

	public List<Traces> getTraces() {
		return traces;
	}
}

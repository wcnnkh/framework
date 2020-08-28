package scw.kdniao;

import java.util.List;

import scw.json.JsonObject;

/**
 * 快递鸟即时查询API返回
 * 
 * @author shuchaowen
 *
 */
public final class EbusinessOrderHandleResponse extends KDNiaoResponse {
	private static final long serialVersionUID = 1L;
	// 订单编号
	private String orderCode;
	// 快递公司编码
	private String shipperCode;
	// 物流运单号
	private String logisticCode;
	private int state;
	private List<Traces> traces;

	EbusinessOrderHandleResponse() {
		super(null);
	}

	public EbusinessOrderHandleResponse(JsonObject json) {
		super(json);

		if (json == null) {
			return;
		}

		this.orderCode = json.getString("OrderCode");
		this.shipperCode = json.getString("ShipperCode");
		this.logisticCode = json.getString("LogisticCode");
		this.state = json.getIntValue("State");
		this.traces = Traces.parseTraces(json.getJsonArray("Traces"));
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

	/**
	 * 物流状态：2-在途中,3-签收,4-问题件
	 * @return
	 */
	public int getState() {
		return state;
	}

	public List<Traces> getTraces() {
		return traces;
	}
}

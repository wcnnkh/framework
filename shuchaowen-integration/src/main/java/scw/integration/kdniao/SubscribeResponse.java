package scw.integration.kdniao;

import scw.json.JsonObject;

public final class SubscribeResponse extends KDNiaoResponse {
	private static final long serialVersionUID = 1L;
	/**
	 * 更新时间YYYY-MM-DD HH24:MM:SS
	 */
	private String updateTime;
	// 订单预计到货时间yyyy-mm-dd（即将上线）
	private String estimatedDeliveryTime;

	SubscribeResponse() {
		super(null);
	}

	public SubscribeResponse(JsonObject json) {
		super(json);
		if (json == null) {
			return;
		}
		this.updateTime = json.getString("UpdateTime");
		this.estimatedDeliveryTime = json.getString("EstimatedDeliveryTime");
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public String getEstimatedDeliveryTime() {
		return estimatedDeliveryTime;
	}
}

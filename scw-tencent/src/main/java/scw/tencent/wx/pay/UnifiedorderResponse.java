package scw.tencent.wx.pay;

import scw.json.JsonObject;

public class UnifiedorderResponse extends WeiXinPayResponse {

	public UnifiedorderResponse(JsonObject target) {
		super(target);
	}

	public TradeType getTradeType() {
		return (TradeType) getEnum("trade_type", TradeType.class);
	}
	
	public String getRawTradeType(){
		return getString("trade_type");
	}

	/**
	 * 微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时
	 * 
	 * @return
	 */
	public String getPrepayId() {
		return getString("prepay_id");
	}
}

package scw.tencent.wx.pay;

import scw.json.JsonObject;

/**
 * {@link https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=13_4&index=3}
 * @author shuchaowen
 *
 */
public class SendredpackResponse extends WeiXinPayResponse{

	public SendredpackResponse(JsonObject target) {
		super(target);
	}

	public String getMchBillno(){
		return getString("mch_billno");
	}
	
	public String getWxappid(){
		return getString("wxappid");
	}
	
	public String getReOpenid(){
		return getString("re_openid");
	}
	
	public int getTotalAmount(){
		return getIntValue("total_amount");
	}
	
	public String getSendListid(){
		return getString("send_listid");
	}
}

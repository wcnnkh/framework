package shuchaowen.web.support.weixin;

import java.util.Map;

import shuchaowen.web.util.WeiXinUtils;

public class WeiXinPay {
	public static final String weixin_unifiedorder_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	
	private String appId;
	private String mch_id;
	private String apiKey;
	
	public WeiXinPay(String appId, String mch_id, String apiKey){
		this.appId = appId;
		this.mch_id = mch_id;
		this.apiKey = apiKey;
	}
	
	/**
	 *  统一下单 字段说明见：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1
	 * @param appid 微信支付分配的公众账号ID（企业号corpid即为此appId）
	 * @param mch_id 微信支付分配的商户号
	 * @param device_info 自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
	 * @param nonce_str 随机字符串，长度要求在32位以内。推荐随机数生成算法
	 * @param sign  通过签名算法计算得出的签名值，详见签名生成算法
	 * @param sign_type 签名类型，默认为MD5，支持HMAC-SHA256和MD5。
	 * @param body 商品简单描述，该字段请按照规范传递，具体请见参数规定
	 * @param detail
	 * @param attach
	 * @param out_trade_no
	 * @param fee_type
	 * @param total_fee
	 * @param spbill_create_ip
	 * @param time_start
	 * @param time_expire
	 * @param goods_tag
	 * @param notify_url
	 * @param trade_type
	 * @param product_id
	 * @param limit_pay
	 * @param openid
	 * @return
	 */
	public String getUnifiedorder( 
			String device_info, String nonce_str,
			String body, String detail, String attach,
			String out_trade_no, String fee_type, int total_fee, String spbill_create_ip,
			String time_start, String time_expire, String goods_tag, String notify_url,
			String trade_type, String product_id, String limit_pay, String openid){
		return WeiXinUtils.getUnifiedorder(appId, mch_id, apiKey, device_info, nonce_str, body, detail, attach, out_trade_no, fee_type, total_fee, spbill_create_ip, time_start, time_expire, goods_tag, notify_url, trade_type, product_id, limit_pay, openid);
	}
	
	/**
	 * 生成微信公众号支付临时订单
	 * @param trade_type 类型  APP JSAPI
	 * @param nonce_str 随机字符串
	 * @param body  商品简单描述
	 * @param out_trade_no  商户系统内部订单号，要求32个字符内、且在同一个商户号下唯一。 详见商户订单号
	 * @param total_fee 订单总金额，单位为分，详见支付金额
	 * @param spbill_create_ip 	APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
	 * @param time_start 	订单生成时间，格式为yyyyMMddHHmmss
	 * @param time_expire   订单失效时间，格式为yyyyMMddHHmmss
	 * @param limit_pay 指定支付方式  上传此参数no_credit--可限制用户不能使用信用卡支付
	 * @param openid
	 * @param notify_url 回调url
	 * @return
	 */
	public String getUnifiedorder(String device_info, String trade_type, String nonce_str, String body,
			String out_trade_no, int total_fee, String spbill_create_ip, 
			String time_start, String time_expire, 
			String limit_pay, String openid, String notify_url){
		return getUnifiedorder(device_info, nonce_str, body, null, null, out_trade_no, "CNY", total_fee, spbill_create_ip, time_start, time_expire, null, notify_url, trade_type, null, limit_pay, openid);
	}
	
	public String getPaySign(Map<String, String> paramMap){
		return WeiXinUtils.getPaySign(paramMap, apiKey);
	}
	
	/**
	 * 获取微信公众号支付签名
	 * @param timeStamp
	 * @param nonceStr
	 * @param prepay_id
	 * @return
	 */
	public String getBrandWCPayRequestSign(String timeStamp, String nonceStr, String prepay_id){
		return WeiXinUtils.getBrandWCPayRequestSign(appId, apiKey, timeStamp, nonceStr, prepay_id);
	}
	
	public String getAppPayRequestSign(String timeStamp, String noceStr, String prepay_id){
		return WeiXinUtils.getAppPayRequestSign(appId, mch_id, apiKey, timeStamp, noceStr, prepay_id);
	}

	public String getAppId() {
		return appId;
	}

	public String getMch_id() {
		return mch_id;
	}

	public String getApiKey() {
		return apiKey;
	}
}
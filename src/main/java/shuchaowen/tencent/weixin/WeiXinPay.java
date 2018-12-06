package shuchaowen.tencent.weixin;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import shuchaowen.connection.http.HttpUtils;
import shuchaowen.core.exception.NotSupportException;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.exception.SignatureException;
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.SignHelp;
import shuchaowen.core.util.StringUtils;
import shuchaowen.tencent.weixin.bean.Unifiedorder;

public final class WeiXinPay {
	private static final String weixin_unifiedorder_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	private static final String DEFAULT_DEVICE_INFO = "WEB";
	
	private final String appId;
	private final String mch_id;
	private final String apiKey;
	private final String sign_type;// 签名类型，默认为MD5，支持HMAC-SHA256和MD5。
	private final Charset charset;
	private final boolean debug;

	public WeiXinPay(String appId, String mch_id, String apiKey, boolean debug) {
		this(appId, mch_id, apiKey, "MD5", Charset.forName("UTF-8"), debug);
	}

	public WeiXinPay(String appId, String mch_id, String apiKey, String sign_type, Charset charset, boolean debug) {
		this.appId = appId;
		this.mch_id = mch_id;
		this.apiKey = apiKey;
		this.sign_type = sign_type.toUpperCase();
		this.charset = charset;
		this.debug = debug;
	}

	/**
	 * 统一下单 字段说明见：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1
	 * 
	 * @param appid
	 *            微信支付分配的公众账号ID（企业号corpid即为此appId）
	 * @param mch_id
	 *            微信支付分配的商户号
	 * @param device_info
	 *            自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
	 * @param nonce_str
	 *            随机字符串，长度要求在32位以内。推荐随机数生成算法
	 * @param timestamp
	 *            时间戳 单位:秒
	 * @param sign
	 *            通过签名算法计算得出的签名值，详见签名生成算法
	 * @param sign_type
	 *            签名类型，默认为MD5，支持HMAC-SHA256和MD5。
	 * @param body
	 *            商品简单描述，该字段请按照规范传递，具体请见参数规定
	 * @param detail
	 * @param attach
	 * @param out_trade_no
	 * @param fee_type
	 *            货币类型 人民币CNY
	 * @param total_fee
	 * @param spbill_create_ip
	 * @param time_start
	 * @param time_expire
	 * @param goods_tag
	 * @param notify_url
	 * @param trade_type
	 *            类型 APP JSAPI
	 * @param product_id
	 * @param limit_pay
	 * @param openid
	 * @return
	 */
	public Unifiedorder getUnifiedorder(String device_info, String nonce_str, long timestamp, String body,
			String detail, String attach, String out_trade_no, String fee_type, int total_fee, String spbill_create_ip,
			String time_start, String time_expire, String goods_tag, String notify_url, String trade_type,
			String product_id, String limit_pay, String openid) {
		String content = getUnifiedorder(device_info, nonce_str, body, detail, attach, out_trade_no, fee_type,
				total_fee, spbill_create_ip, time_start, time_expire, goods_tag, notify_url, trade_type, product_id,
				limit_pay, openid);
		if(debug){
			Logger.debug(this.getClass().getName(), "统一下单接口返回：" + content);
		}
		
		Map<String, String> map = WeiXinUtils.xmlToMap(content);
		if (map == null) {
			throw new ShuChaoWenRuntimeException("服务器错误");
		}

		if (!"SUCCESS".equals(map.get("return_code"))) {
			throw new ShuChaoWenRuntimeException(content);
		}

		if (!"SUCCESS".equals(map.get("result_code"))) {
			throw new ShuChaoWenRuntimeException(content);
		}
		
		if(!checkSign(map)){
			throw new SignatureException(content);
		}
		
		String prepay_id = map.get("prepay_id");
		Unifiedorder unifiedorder = new Unifiedorder();
		unifiedorder.setTimestamp(timestamp);
		unifiedorder.setNonce_str(nonce_str);
		unifiedorder.setPaySign(WeiXinUtils.getAppPayRequestSign(appId, mch_id, apiKey, unifiedorder.getTimestamp(),
				nonce_str, prepay_id));
		unifiedorder.setPrepay_id(prepay_id);
		return unifiedorder;
	}

	/**
	 * 生成微信公众号支付临时订单
	 * 
	 * @param trade_type
	 *            类型 APP JSAPI
	 * @param body
	 *            商品简单描述
	 * @param out_trade_no
	 *            商户系统内部订单号，要求32个字符内、且在同一个商户号下唯一。 详见商户订单号
	 * @param fee_type
	 *            货币类型 人民币CNY
	 * @param total_fee
	 *            订单总金额，单位为分，详见支付金额
	 * @param spbill_create_ip
	 *            APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
	 * @param time_start
	 *            订单生成时间，格式为yyyyMMddHHmmss
	 * @param time_expire
	 *            订单失效时间，格式为yyyyMMddHHmmss
	 * @param limit_pay
	 *            指定支付方式 上传此参数no_credit--可限制用户不能使用信用卡支付
	 * @param openid
	 * @param notify_url
	 *            回调url
	 * @return
	 */
	public Unifiedorder getDefaultUnifiedorder(String trade_type, String body, String out_trade_no,
			String fee_type, int total_fee, String spbill_create_ip, String time_start, String time_expire,
			String limit_pay, String openid, String notify_url) {
		return getUnifiedorder(DEFAULT_DEVICE_INFO, StringUtils.getRandomStr(16), System.currentTimeMillis() / 1000, body, null,
				null, out_trade_no, fee_type, total_fee, spbill_create_ip, time_start, time_expire, null, notify_url,
				trade_type, null, limit_pay, openid);
	}

	/**
	 * 生成一个简单的订单
	 * 
	 * @param device_info
	 *            自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
	 * @param trade_type
	 *            类型 APP JSAPI
	 * @param name
	 * @param orderId
	 * @param amount
	 * @param ip
	 * @param notify_url
	 * @return
	 */
	public Unifiedorder getSimpleUnifiedorder(String trade_type, String name, String orderId,
			int amount, String ip, String notify_url) {
		return getDefaultUnifiedorder(trade_type, name, orderId, "CNY", amount, ip, null, null, null, null,
				notify_url);
	}

	public String getPaySign(Map<String, String> paramMap) {
		return WeiXinUtils.getPaySign(paramMap, apiKey);
	}

	/**
	 * 获取微信公众号支付签名
	 * 
	 * @param timeStamp
	 * @param nonceStr
	 * @param prepay_id
	 * @return
	 */
	public String getBrandWCPayRequestSign(String timeStamp, String nonceStr, String prepay_id) {
		return WeiXinUtils.getBrandWCPayRequestSign(appId, apiKey, timeStamp, nonceStr, prepay_id);
	}

	public String getAppPayRequestSign(long timeStamp, String noceStr, String prepay_id) {
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

	/**
	 * 统一下单 字段说明见：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1
	 * 
	 * @param appid
	 *            微信支付分配的公众账号ID（企业号corpid即为此appId）
	 * @param mch_id
	 *            微信支付分配的商户号
	 * @param device_info
	 *            自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
	 * @param nonce_str
	 *            随机字符串，长度要求在32位以内。推荐随机数生成算法
	 * @param sign
	 *            通过签名算法计算得出的签名值，详见签名生成算法
	 * @param sign_type
	 *            签名类型，默认为MD5，支持HMAC-SHA256和MD5。
	 * @param body
	 *            商品简单描述，该字段请按照规范传递，具体请见参数规定
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
	public String getUnifiedorder(String device_info, String nonce_str, String body, String detail, String attach,
			String out_trade_no, String fee_type, int total_fee, String spbill_create_ip, String time_start,
			String time_expire, String goods_tag, String notify_url, String trade_type, String product_id,
			String limit_pay, String openid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("appid", appId);
		map.put("mch_id", mch_id);
		map.put("device_info", device_info);
		map.put("nonce_str", nonce_str);
		map.put("sign_type", sign_type);
		map.put("body", body);
		map.put("detail", detail);
		map.put("attach", attach);
		map.put("out_trade_no", out_trade_no);
		map.put("fee_type", fee_type);
		map.put("total_fee", total_fee + "");
		map.put("spbill_create_ip", spbill_create_ip);
		map.put("time_start", time_start);
		map.put("time_expire", time_expire);
		map.put("goods_tag", goods_tag);
		map.put("notify_url", notify_url);
		map.put("trade_type", trade_type);
		map.put("product_id", product_id);
		map.put("limit_pay", limit_pay);
		map.put("openid", openid);
		String[] keys = map.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		String v;
		String k;
		StringBuilder sb = new StringBuilder();
		Element root = DocumentHelper.createElement("xml");
		Element element;
		for (int i = 0; i < keys.length; i++) {
			k = keys[i];
			if (k == null) {
				continue;
			}

			v = map.get(k);
			if (v == null) {
				continue;
			}

			element = DocumentHelper.createElement(k);
			element.setText(v);
			root.add(element);
			if (sb.length() > 0) {
				sb.append("&");
			}

			sb.append(k).append("=").append(v);
		}
		sb.append("&key=").append(apiKey);
		if(debug){
			Logger.debug(this.getClass().getName(), "签名字符串：" + sb.toString());
		}
		element = DocumentHelper.createElement("sign");
		String sign = toSign(sb.toString());
		if(debug){
			Logger.debug(this.getClass().getName(), "签名：" + sign);
		}
		element.setText(sign);
		root.add(element);
		String xmlContent = root.asXML();
		if(debug){
			Logger.debug(this.getClass().getName(), "签名XML：" + xmlContent);
		}
		
		return HttpUtils.doPost(weixin_unifiedorder_url, null, xmlContent);
	}
	
	public boolean checkSign(Map<String, String> params){
		Map<String, String> cloneParams = new HashMap<String, String>(params);
		String sign = cloneParams.get("sign");
		if(sign == null){
			return false;
		}
		
		cloneParams.remove("sign");
		StringBuilder checkStr = SignHelp.getShotParamsStr(cloneParams);
		checkStr.append("&key=").append(apiKey);
		return sign.equals(toSign(checkStr.toString()));
	}
	
	private String toSign(String str){
		if("MD5".equalsIgnoreCase(sign_type)){
			return SignHelp.md5UpperStr(str, charset.name());
		}else if("HMAC-SHA256".equalsIgnoreCase(sign_type)){
			//TODO
			throw new NotSupportException(sign_type);
		}else{
			throw new ShuChaoWenRuntimeException("不支持的签名方式:" + sign_type);
		}
	}
}
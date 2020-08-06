package scw.tencent.wx;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import scw.core.Constants;
import scw.core.utils.StringUtils;
import scw.http.HttpUtils;
import scw.http.MediaType;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.json.JSONUtils;
import scw.lang.NestedRuntimeException;
import scw.lang.NotSupportedException;
import scw.lang.ParameterException;
import scw.lang.SignatureException;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.MimeTypeUtils;
import scw.net.ssl.SSLContexts;
import scw.security.SignatureUtils;
import scw.tencent.wx.pay.UnifiedOrderResponse;
import scw.util.RandomUtils;
import scw.xml.XMLUtils;

public final class WeiXinPay {
	private static Logger logger = LoggerFactory.getLogger(WeiXinPay.class);
	private static final String weixin_unifiedorder_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	private static final String DEFAULT_DEVICE_INFO = "WEB";
	private static final String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";

	private final String appId;
	private final String mch_id;
	private final String apiKey;
	private final String sign_type;// 签名类型，默认为MD5，支持HMAC-SHA256和MD5。
	private final String charsetName;
	private String certTrustFile;
	private SSLSocketFactory sslSocketFactory;

	public WeiXinPay(String appId, String mch_id, String apiKey) {
		this(appId, mch_id, apiKey, "MD5", Constants.DEFAULT_CHARSET_NAME, null);
	}

	public WeiXinPay(String appId, String mch_id, String apiKey, String certTrustFile) {
		this(appId, mch_id, apiKey, "MD5", Constants.DEFAULT_CHARSET_NAME, certTrustFile);
	}

	public WeiXinPay(String appId, String mch_id, String apiKey, String sign_type, String charsetName,
			String certTrustFile) {
		this.appId = appId;
		this.mch_id = mch_id;
		this.apiKey = apiKey;
		this.sign_type = sign_type.toUpperCase();
		this.charsetName = charsetName;
		this.certTrustFile = certTrustFile;
		this.sslSocketFactory = initSSLSocketFactory(certTrustFile);
	}

	private SSLSocketFactory initSSLSocketFactory(String certTrustFile) {
		if (StringUtils.isEmpty(certTrustFile)) {
			return null;
		}

		Resource resource = ResourceUtils.getResourceOperations().getResource(certTrustFile);
		if(!resource.exists()){
			return null;
		}
		
		InputStream is = ResourceUtils.getInputStream(resource);
		char[] password = mch_id.toCharArray();
		try {
			return SSLContexts.custom().loadKeyMaterial(is, password, password).build().getSocketFactory();
		} catch (Exception e) {
			throw new NestedRuntimeException(certTrustFile, e);
		}
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
		Map<String, String> map = getUnifiedorder(device_info, nonce_str, body, detail, attach, out_trade_no, fee_type,
				total_fee, spbill_create_ip, time_start, time_expire, goods_tag, notify_url, trade_type, product_id,
				limit_pay, openid);
		String prepay_id = map.get("prepay_id");
		Unifiedorder unifiedorder = new Unifiedorder();
		unifiedorder.setTimestamp(timestamp);
		unifiedorder.setNonce_str(nonce_str);
		if (!StringUtils.isEmpty(openid)) {
			unifiedorder.setPaySign(WeiXinUtils.getBrandWCPayRequestSign(appId, apiKey,
					String.valueOf(unifiedorder.getTimestamp()), nonce_str, prepay_id));
		} else {
			unifiedorder.setPaySign(WeiXinUtils.getAppPayRequestSign(appId, mch_id, apiKey, unifiedorder.getTimestamp(),
					nonce_str, prepay_id));
		}

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
	public Unifiedorder getDefaultUnifiedorder(String trade_type, String body, String out_trade_no, String fee_type,
			int total_fee, String spbill_create_ip, String time_start, String time_expire, String limit_pay,
			String openid, String notify_url) {
		return getUnifiedorder(DEFAULT_DEVICE_INFO, RandomUtils.getRandomStr(16), System.currentTimeMillis() / 1000,
				body, null, null, out_trade_no, fee_type, total_fee, spbill_create_ip, time_start, time_expire, null,
				notify_url, trade_type, null, limit_pay, openid);
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
	public Unifiedorder getSimpleUnifiedorder(String trade_type, String name, String orderId, int amount, String ip,
			String notify_url) {
		return getDefaultUnifiedorder(trade_type, name, orderId, "CNY", amount, ip, null, null, null, null, notify_url);
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
	public UnifiedOrderResponse getUnifiedorder(String device_info, String nonce_str, String body, String detail,
			String attach, String out_trade_no, String fee_type, int total_fee, String spbill_create_ip,
			String time_start, String time_expire, String goods_tag, String notify_url, String trade_type,
			String product_id, String limit_pay, String openid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("appid", appId);
		map.put("mch_id", mch_id);
		map.put("device_info", device_info);
		map.put("nonce_str", nonce_str);
		map.put("sign_type", sign_type);
		if (body.length() >= 128) {
			map.put("body", body.substring(0, 120) + "...");
		} else {
			map.put("body", body);
		}

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
		Map<String, String> responseMap = invoke(weixin_unifiedorder_url, map, false);
		return new UnifiedOrderResponse(responseMap);
	}

	public boolean checkSign(Map<String, String> params) {
		Map<String, String> cloneParams = new HashMap<String, String>(params);
		String sign = cloneParams.get("sign");
		if (sign == null) {
			return false;
		}

		cloneParams.remove("sign");
		StringBuilder checkStr = SignatureUtils.formatSortParams(cloneParams);
		checkStr.append("&key=").append(apiKey);
		String mySign = toSign(checkStr.toString());
		boolean b = sign.equals(mySign);
		if (!b) {
			logger.error("签名检验失败：{}------>{}", JSONUtils.toJSONString(params), mySign);
		}
		return b;
	}

	public SSLSocketFactory getSslSocketFactory() {
		return sslSocketFactory;
	}

	public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
		this.sslSocketFactory = sslSocketFactory;
	}

	/**
	 * 向微信支付服务器发送请求
	 * 
	 * @param url
	 * @param parameterMap
	 * @param isCertTrustFile
	 *            请求中是否包含证书
	 * @return
	 */
	public Map<String, String> invoke(String url, Map<String, ?> parameterMap, boolean isCertTrustFile) {
		if (isCertTrustFile && StringUtils.isEmpty(certTrustFile)) {
			throw new ParameterException("未配置API证书目录");
		}

		String[] keys = parameterMap.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		StringBuilder sb = new StringBuilder();
		Document document = XMLUtils.newDocumentBuilder().newDocument();
		Element element = document.createElement("xml");
		for (int i = 0; i < keys.length; i++) {
			String k = keys[i];
			if (k == null) {
				continue;
			}

			Object v = parameterMap.get(k);
			if (v == null) {
				continue;
			}

			String value = v.toString();
			Element c = document.createElement(k);
			c.setTextContent(value);
			element.appendChild(c);
			if (sb.length() > 0) {
				sb.append("&");
			}

			sb.append(k).append("=").append(value);
		}
		sb.append("&key=").append(apiKey);
		logger.debug("微信支付签名字符串：{}", sb);
		String sign = toSign(sb.toString());
		Element c = document.createElement("sign");
		c.setTextContent(sign);
		element.appendChild(c);
		final String content = XMLUtils.toString(element);

		logger.debug("微信支付请求xml内容:{}", content);

		String res = HttpUtils.getHttpClient().post(String.class, url, sslSocketFactory, content,
				new MediaType(MimeTypeUtils.APPLICATION_XML, charsetName)).getBody();
		if (res == null) {
			throw new RuntimeException("请求：" + url + "失败");
		}

		logger.debug("请求：{}，返回{}", url, res);

		Map<String, String> map = XMLUtils.xmlToMap(res);
		String return_code = map.get("return_code");
		if (!"SUCCESS".equals(return_code)) {
			throw new RuntimeException(res);
		}

		String result_code = map.get("result_code");
		if (!"SUCCESS".equals(result_code)) {
			throw new RuntimeException(res);
		}

		if (!checkSign(map)) {
			throw new SignatureException("签名错误");
		}

		return map;
	}

	private String toSign(String str) {
		if ("MD5".equalsIgnoreCase(sign_type)) {
			return SignatureUtils.md5(str, charsetName).toUpperCase();
		} else if ("HMAC-SHA256".equalsIgnoreCase(sign_type)) {
			// TODO
			throw new NotSupportedException(sign_type);
		} else {
			throw new NotSupportedException("不支持的签名方式:" + sign_type);
		}
	}

	public Map<String, String> refund(String transaction_id, String out_trade_no, String out_refund_no, int total_fee,
			int refund_fee, String refund_fee_type, String refund_desc, String notify_url) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("appid", appId);
		map.put("mch_id", mch_id);
		map.put("nonce_str", RandomUtils.getRandomStr(10));
		map.put("sign_type", sign_type);
		map.put("transaction_id", transaction_id);
		map.put("out_trade_no", out_trade_no);
		map.put("out_refund_no", out_refund_no);
		map.put("total_fee", total_fee + "");
		map.put("refund_fee", refund_fee + "");
		map.put("refund_fee_type", refund_fee_type);
		map.put("refund_desc", refund_desc);
		map.put("notify_url", notify_url);
		return invoke(REFUND_URL, map, true);
	}
}
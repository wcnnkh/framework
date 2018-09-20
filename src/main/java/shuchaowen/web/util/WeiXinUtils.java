package shuchaowen.web.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import shuchaowen.core.util.SHA1;
import shuchaowen.core.util.SignHelp;
import shuchaowen.web.util.http.HttpPost;
import shuchaowen.web.util.http.core.Http;

/**
 * @author shuchaowen
 */
public abstract class WeiXinUtils {
	public static final String weixin_get_user_info_url = "https://api.weixin.qq.com/cgi-bin/user/info";
	public static final String weixin_authorize_url = "https://open.weixin.qq.com/connect/oauth2/authorize";
	public static final String weixin_qrconnect_url = "https://open.weixin.qq.com/connect/qrconnect";
	public static final String weixin_get_web_access_token = "https://api.weixin.qq.com/sns/oauth2/access_token";
	public static final String weixin_get_web_refresh_access_token = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
	public static final String weixin_get_web_userinfo = "https://api.weixin.qq.com/sns/userinfo";
	public static final String weixin_get_web_token = "https://api.weixin.qq.com/cgi-bin/token";
	public static final String weixin_get_web_ticket = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
	
	public static final String weixin_unifiedorder_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

	public static String getWebUserInfo(String openId, String web_access_token) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", web_access_token);
		paramMap.put("openid", openId);
		paramMap.put("lang", "zh_CN ");
		return HttpPost.invoke(weixin_get_web_userinfo, paramMap);
	}
	
	/**
	 * 授权登录
	 * @param appid
	 * @param redirect_uri
	 * @param scope
	 * @param state
	 * @return
	 */
	public static String authorizeUlr(String appid, String redirect_uri, String scope, String state){
		StringBuilder sb = new StringBuilder(weixin_authorize_url);
		sb.append("?appid=").append(appid);
		sb.append("&redirect_uri=").append(Http.encode(redirect_uri));
		sb.append("&response_type=code");
		sb.append("&scope=").append(scope);
		sb.append("&state=").append(state);
		sb.append("#wechat_redirect");
		return sb.toString();
	}
	
	/**
	 * 扫码登录
	 * @param appid
	 * @param redirect_uri
	 * @param scope
	 * @param state
	 * @return
	 */
	public static String qrcodeAuthorizeUrl(String appid, String redirect_uri, String scope, String state){
		StringBuilder sb = new StringBuilder(weixin_qrconnect_url);
		sb.append("?appid=").append(appid);
		sb.append("&redirect_uri=").append(Http.encode(redirect_uri));
		sb.append("&response_type=code");
		sb.append("&scope=").append(scope);
		sb.append("&state=").append(state);
		sb.append("#wechat_redirect");
		return sb.toString();
	}

	public static String getWebToken(String appId, String appsecret) {
		String url = weixin_get_web_token
				+ "?grant_type=client_credential&appid=" + appId + "&secret="
				+ appsecret;
		return HttpPost.invoke(url);
	}
	
	public static String getWebTicket(String token) {
		return HttpPost.invoke(weixin_get_web_ticket + "?access_token=" + token
				+ "&type=jsapi");
	}

	public static int getRefreshExpires(int refreshCount) {
		switch (refreshCount) {
		case 0:
			return 7 * 24 * 3600;
		case 1:
			return 30 * 24 * 3600;
		case 2:
			return 60 * 24 * 3600;
		case 3:
			return 90 * 24 * 3600;
		default:
			return 7 * 24 * 3600;
		}
	}

	public static String getWebRefreshAccessToken(String appId, String refreshToken) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("appid", appId);
		map.put("grant_type", "refresh_token");
		map.put("refresh_token", refreshToken);
		return HttpPost.invoke(weixin_get_web_refresh_access_token, map);
	}

	public static String getWebAccessToken(String appId, String appsecret, String code) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("appid", appId);
		map.put("secret", appsecret);
		map.put("code", code);
		map.put("grant_type", "authorization_code");
		String jsonData = HttpPost.invoke(weixin_get_web_access_token, map);
		return jsonData;
	}

	/**
	 * 获取jssdk签名
	 * @param url
	 * @param noncestr
	 * @param timestamp
	 * @return
	 */
	public static String getJSSDKSignature(String ticket, String url, String noncestr,
			String timestamp) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("noncestr", noncestr);
		map.put("timestamp", timestamp);
		map.put("url", url);
		map.put("jsapi_ticket", ticket);

		String[] keys = map.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			sb.append(key).append("=").append(map.get(key));
			if (i < keys.length - 1) {
				sb.append("&");
			}
		}
		return SHA1.sha1(sb.toString());
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
	public static String getUnifiedorder(String appId, String mch_id, String apiKey,
			String device_info, String nonce_str,
			String body, String detail, String attach,
			String out_trade_no, String fee_type, int total_fee, String spbill_create_ip,
			String time_start, String time_expire, String goods_tag, String notify_url,
			String trade_type, String product_id, String limit_pay, String openid){
		Map<String, String> map = new HashMap<String, String>();
		map.put("appid", appId);
		map.put("mch_id", mch_id);
		map.put("device_info", device_info);
		map.put("nonce_str", nonce_str);
		map.put("sign_type", "MD5");
		map.put("body", body);
		map.put("detail", detail);
		map.put("attach", attach);
		map.put("out_trade_no", out_trade_no);
		map.put("fee_type", fee_type);
		map.put("total_fee", total_fee +"");
		map.put("spbill_create_ip",spbill_create_ip);
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
		for(int i=0; i<keys.length; i++){ 
			k = keys[i];
			if(k == null){
				continue;
			}
			
			v = map.get(k);
			if(v == null){
				continue;
			}
			
			element = DocumentHelper.createElement(k);
			element.setText(v);
			root.add(element);
			if(sb.length() > 0){
				sb.append("&");
			}
			
			sb.append(k).append("=").append(v);
		}
		sb.append("&key=").append(apiKey);
		element = DocumentHelper.createElement("sign");
		element.setText(SignHelp.md5UpperStr(sb.toString(), "UTF-8"));
		root.add(element);
		try {
			return HttpPost.invoke(weixin_unifiedorder_url, root.asXML().getBytes("UTF-8"), null, 5000, 5000);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getPaySign(Map<String, String> paramMap, String apiKey){
		StringBuilder sb = SignHelp.getShotParamsStr(paramMap);
		sb.append("&key=").append(apiKey);
		return SignHelp.md5UpperStr(sb.toString(), "UTF-8");
	}
	
	/**
	 * 获取微信公众号支付签名
	 * @param timeStamp
	 * @param nonceStr
	 * @param prepay_id
	 * @return
	 */
	public static String getBrandWCPayRequestSign(String appId, String apiKey, String timeStamp, String nonceStr, String prepay_id){
		Map<String, String> map = new HashMap<String, String>();
		map.put("appId", appId);
		map.put("timeStamp", timeStamp);
		map.put("nonceStr", nonceStr);
		map.put("package", "prepay_id=" + prepay_id);
		map.put("signType", "MD5");
		return getPaySign(map, apiKey);
	}
	
	public static String getAppPayRequestSign(String appId, String mch_id, String apiKey, String timeStamp, String noceStr, String prepay_id){
		Map<String, String> signMap = new HashMap<String, String>();
		signMap.put("appid", appId);
		signMap.put("partnerid", mch_id);
		signMap.put("prepayid", prepay_id);
		signMap.put("package", "Sign=WXPay");
		signMap.put("noncestr", noceStr);
		signMap.put("timestamp", timeStamp);
		return getPaySign(signMap, apiKey);
	}
	
	public static Map<String, String> xmlToMap(String xml){
		Document document = null;
		try {
			document = DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		if(document == null){
			return null;
		}
		
		Element root = document.getRootElement();
		int size = root.nodeCount();
		Map<String, String> map = new HashMap<String, String>();
		for(int i=0; i<size; i++){
			Node node = root.node(i);
			String k = node.getName();
			if(k == null){
				continue;
			}
			
			map.put(k, node.getText());
		}
		return map;
	}
}

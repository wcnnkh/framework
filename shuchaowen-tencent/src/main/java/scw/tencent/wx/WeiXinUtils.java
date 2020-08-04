package scw.tencent.wx;

import java.util.HashMap;
import java.util.Map;

import scw.http.HttpUtils;
import scw.http.MediaType;
import scw.json.JSONUtils;
import scw.json.JsonObject;
import scw.oauth2.AccessToken;
import scw.security.SignatureUtils;
import scw.security.Token;
import scw.tencent.wx.miniprogram.WeappTemplateMsg;

/**
 * @author shuchaowen
 */
public final class WeiXinUtils {
	private static final String CODE_NAME = "errcode";
	public static final String weixin_authorize_url = "https://open.weixin.qq.com/connect/oauth2/authorize";
	public static final String weixin_qrconnect_url = "https://open.weixin.qq.com/connect/qrconnect";

	private WeiXinUtils() {
	};

	/**
	 * 授权登录
	 * 
	 * @param appid
	 * @param redirect_uri
	 * @param scope
	 * @param state
	 * @return
	 */
	public static String authorizeUlr(String appid, String redirect_uri, String scope, String state) {
		StringBuilder sb = new StringBuilder(weixin_authorize_url);
		sb.append("?appid=").append(appid);
		sb.append("&redirect_uri=").append(HttpUtils.encode(redirect_uri));
		sb.append("&response_type=code");
		sb.append("&scope=").append(scope);
		sb.append("&state=").append(state);
		sb.append("#wechat_redirect");
		return sb.toString();
	}

	/**
	 * 扫码登录
	 * 
	 * @param appid
	 * @param redirect_uri
	 * @param scope
	 * @param state
	 * @return
	 */
	public static String qrcodeAuthorizeUrl(String appid, String redirect_uri, String scope, String state) {
		StringBuilder sb = new StringBuilder(weixin_qrconnect_url);
		sb.append("?appid=").append(appid);
		sb.append("&redirect_uri=").append(HttpUtils.encode(redirect_uri));
		sb.append("&response_type=code");
		sb.append("&scope=").append(scope);
		sb.append("&state=").append(state);
		sb.append("#wechat_redirect");
		return sb.toString();
	}

	public static String getPaySign(Map<String, String> paramMap, String apiKey) {
		StringBuilder sb = SignatureUtils.formatSortParams(paramMap);
		sb.append("&key=").append(apiKey);
		return SignatureUtils.md5(sb.toString(), "UTF-8").toUpperCase();
	}

	/**
	 * 获取微信公众号支付签名
	 * 
	 * @param timeStamp
	 * @param nonceStr
	 * @param prepay_id
	 * @return
	 */
	public static String getBrandWCPayRequestSign(String appId, String apiKey, String timeStamp, String nonceStr,
			String prepay_id) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("appId", appId);
		map.put("timeStamp", timeStamp);
		map.put("nonceStr", nonceStr);
		map.put("package", "prepay_id=" + prepay_id);
		map.put("signType", "MD5");
		return getPaySign(map, apiKey);
	}

	public static String getAppPayRequestSign(String appId, String mch_id, String apiKey, long timeStamp,
			String noceStr, String prepay_id) {
		Map<String, String> signMap = new HashMap<String, String>();
		signMap.put("appid", appId);
		signMap.put("partnerid", mch_id);
		signMap.put("prepayid", prepay_id);
		signMap.put("package", "Sign=WXPay");
		signMap.put("noncestr", noceStr);
		signMap.put("timestamp", timeStamp + "");
		return getPaySign(signMap, apiKey);
	}

	private static boolean checkResponse(JsonObject json) {
		if (json == null) {
			return false;
		}

		return json.getIntValue(CODE_NAME) == 0;
	}

	public static JsonObject doPost(String url, Map<String, ?> data) {
		String content = HttpUtils.getHttpClient().post(String.class, url, data, MediaType.APPLICATION_FORM_URLENCODED).getBody();
		JsonObject json = JSONUtils.parseObject(content);
		if (!checkResponse(json)) {
			throw new RuntimeException(
					"url=" + url + ", data=" + JSONUtils.toJSONString(data) + ", response=" + content);
		}
		return json;
	}

	public static JsonObject doGet(String url) {
		String content = HttpUtils.getHttpClient().get(String.class, url).getBody();
		JsonObject json = JSONUtils.parseObject(content);
		if (!checkResponse(json)) {
			throw new RuntimeException("url=" + url + ", response=" + content);
		}
		return json;
	}

	public static AccessToken getAccessToken(String appId, String appSecret) {
		return getAccessToken("client_credential", appId, appSecret);
	}

	public static AccessToken getAccessToken(String grant_type, String appId, String appSecret) {
		StringBuilder sb = new StringBuilder("https://api.weixin.qq.com/cgi-bin/token");
		sb.append("?grant_type=").append(grant_type);
		sb.append("&appid=").append(appId);
		sb.append("&secret=").append(appSecret);
		JsonObject json = doGet(sb.toString());
		return parseAccessToken(json);
	}

	private static AccessToken parseAccessToken(JsonObject json) {
		return new AccessToken(new Token(json.getString("access_token"), json.getIntValue("expires_in")), null,
				new Token(json.getString("refresh_token"), 30 * 24 * 3600), json.getString("scope"), null);
	}

	public static Token getJsApiTicket(String access_token) {
		return getTicket(access_token, "jsapi");
	}

	public static Token getTicket(String access_token, String type) {
		StringBuilder sb = new StringBuilder("https://api.weixin.qq.com/cgi-bin/ticket/getticket");
		sb.append("?access_token=").append(access_token);
		sb.append("&type=").append(type);
		JsonObject json = doGet(sb.toString());
		return new Token(json.getString("ticket"), json.getIntValue("expires_in"));
	}

	public static UserAccessToken getUserAccesstoken(String appid, String appsecret, String code) {
		Map<String, String> map = new HashMap<String, String>(4, 1);
		map.put("appid", appid);
		map.put("secret", appsecret);
		map.put("code", code);
		map.put("grant_type", "authorization_code");
		JsonObject json = doPost("https://api.weixin.qq.com/sns/oauth2/access_token", map);
		return new UserAccessToken(parseAccessToken(json), json.getString("openid"));
	}

	public static UserAccessToken refreshWebUserAccesstoken(String appid, String refresh_token) {
		Map<String, String> map = new HashMap<String, String>(4, 1);
		map.put("appid", appid);
		map.put("grant_type", "refresh_token");
		map.put("refresh_token", refresh_token);
		JsonObject json = doPost("https://api.weixin.qq.com/sns/oauth2/refresh_token", map);
		return new UserAccessToken(parseAccessToken(json), json.getString("openid"));
	}

	public static Userinfo getUserinfo(String openid, String user_access_token) {
		return getUserinfo(openid, user_access_token, "zh_CN");
	}

	public static Userinfo getUserinfo(String openid, String user_access_token, String lang) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", user_access_token);
		paramMap.put("openid", openid);
		paramMap.put("lang", "zh_CN");
		JsonObject json = doPost("https://api.weixin.qq.com/sns/userinfo", paramMap);
		return new Userinfo(json.getString("openid"), json.getString("nickname"), json.getIntValue("sex"),
				json.getString("province"), json.getString("city"), json.getString("country"),
				json.getString("headimgurl"), json.getString("privilege"), json.getString("unionid"));
	}

	public BaseResponse sendUniformMessage(String access_token, String touser, WeappTemplateMsg weapp_template_msg,
			MpTemplateMsg mp_template_msg) {
		Map<String, Object> map = new HashMap<String, Object>(4, 1);
		map.put("touser", touser);
		map.put("weapp_template_msg", weapp_template_msg);
		map.put("mp_template_msg", mp_template_msg);
		JsonObject json = WeiXinUtils.doPost(
				"https://api.weixin.qq.com/cgi-bin/message/wxopen/template/uniform_send?access_token=" + access_token,
				map);
		return new BaseResponse(json);
	}
}

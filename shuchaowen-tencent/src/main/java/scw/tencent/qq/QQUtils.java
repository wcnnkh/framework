package scw.tencent.qq;

import java.util.HashMap;
import java.util.Map;

import scw.json.JSONUtils;
import scw.json.JsonObject;
import scw.net.http.HttpUtils;

public final class QQUtils {
	private static final String callbackPrefix = "callback( ";

	public static final String qq_authorizeUrl = "https://graph.qq.com/oauth2.0/authorize";
	public static final String qq_get_pc_token = "https://graph.qq.com/oauth2.0/token";
	public static final String qq_get_wap_token = "https://graph.z.qq.com/moc2/token";
	public static final String qq_get_pc_openid = "https://graph.qq.com/oauth2.0/me";
	public static final String qq_get_wap_openid = "https://graph.z.qq.com/moc2/me";
	public static final String qq_get_user_info = "https://graph.qq.com/user/get_user_info";

	private QQUtils() {
	};

	/**
	 * 登录成功后
	 * 
	 * @param redirect_uri
	 *            登录成功后的回调地址
	 * @return
	 */
	public static String getAuthorizeUrl(String appId, String redirect_uri, String state) {
		StringBuilder sb = new StringBuilder(qq_authorizeUrl);
		sb.append("?");
		sb.append("response_type=code");
		sb.append("&client_id=").append(appId);
		sb.append("&redirect_uri=").append(HttpUtils.encode(redirect_uri));
		sb.append("&state=" + state);
		return sb.toString();
	}

	public static String getPCTokenData(String appId, String appKey, String redirect_uri, String code) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("grant_type", "authorization_code");
		map.put("client_id", appId);
		map.put("client_secret", appKey);
		map.put("redirect_uri", redirect_uri);
		map.put("code", code);
		return HttpUtils.getHttpClient().postForFrom(qq_get_pc_token, map);
	}

	public static String formatCallBackPrefix(String data) {
		if (data.startsWith(callbackPrefix)) {
			return data.substring(callbackPrefix.length(), data.length() - 2);
		}
		return data;
	}

	public static String getOpenId(String access_token) {
		StringBuilder sb = new StringBuilder(qq_get_pc_openid);
		sb.append("?access_token=").append(access_token);
		String data = HttpUtils.getHttpClient().get(sb.toString());
		data = formatCallBackPrefix(data);
		JsonObject jsonObject = JSONUtils.parseObject(data);
		return jsonObject.getString("openid");
	}

	public static String getServerUserInfo(String appId, String access_token, String openId) {
		StringBuilder sb = new StringBuilder(qq_get_user_info);
		sb.append("?access_token=").append(access_token);
		sb.append("&oauth_consumer_key=").append(appId);
		sb.append("&openid=").append(openId);
		return HttpUtils.getHttpClient().get(sb.toString());
	}
}

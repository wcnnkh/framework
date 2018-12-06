package shuchaowen.tencent.weixin;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.alibaba.fastjson.JSONObject;

import shuchaowen.common.utils.SignHelp;
import shuchaowen.connection.http.HttpUtils;

/**
 * @author shuchaowen
 */
public final class WeiXinUtils {
	public static final String weixin_authorize_url = "https://open.weixin.qq.com/connect/oauth2/authorize";
	public static final String weixin_qrconnect_url = "https://open.weixin.qq.com/connect/qrconnect";

	private WeiXinUtils(){};
	
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
		sb.append("&redirect_uri=").append(HttpUtils.encode(redirect_uri));
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
		sb.append("&redirect_uri=").append(HttpUtils.encode(redirect_uri));
		sb.append("&response_type=code");
		sb.append("&scope=").append(scope);
		sb.append("&state=").append(state);
		sb.append("#wechat_redirect");
		return sb.toString();
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
	
	public static String getAppPayRequestSign(String appId, String mch_id, String apiKey, long timeStamp, String noceStr, String prepay_id){
		Map<String, String> signMap = new HashMap<String, String>();
		signMap.put("appid", appId);
		signMap.put("partnerid", mch_id);
		signMap.put("prepayid", prepay_id);
		signMap.put("package", "Sign=WXPay");
		signMap.put("noncestr", noceStr);
		signMap.put("timestamp", timeStamp + "");
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
	
	public static boolean isError(JSONObject json){
		return json.containsKey("errcode") && json.getIntValue("errcode") != 0;
	}
}

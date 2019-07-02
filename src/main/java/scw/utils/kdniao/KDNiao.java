package scw.utils.kdniao;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import scw.core.Base64;
import scw.core.io.ByteArray;
import scw.core.net.ContentType;
import scw.core.net.DefaultContentType;
import scw.core.net.NetworkUtils;
import scw.core.net.http.BodyRequest;
import scw.core.net.http.HttpRequest;
import scw.core.net.http.HttpUtils;
import scw.core.net.http.Method;
import scw.core.utils.SignUtils;
import scw.json.JSONUtils;

/**
 * 快递鸟接口
 * 
 * @author shuchaowen
 *
 */
public class KDNiao {
	private static final String CHARSET_NAME = "UTF-8";

	private final String businessId;
	private final String apiKey;
	private final boolean sandbox;
	private final boolean https;

	public KDNiao(String businessId, String apiKey, boolean sandbox, boolean https) {
		this.businessId = businessId;
		this.apiKey = apiKey;
		this.sandbox = sandbox;
		this.https = https;
	}

	public final String getBusinessId() {
		return businessId;
	}

	public final String getApiKey() {
		return apiKey;
	}

	public final boolean isSandbox() {
		return sandbox;
	}

	public final boolean isHttps() {
		return https;
	}

	public String getRequestParameter(String requestType, Map<String, Object> businessParameterMap) {
		String businessParameter = JSONUtils.toJSONString(businessParameterMap);
		Map<String, String> parameterMap = new HashMap<String, String>(8, 1);
		parameterMap.put("RequestData", businessParameter);
		parameterMap.put("EBusinessID", businessId);
		parameterMap.put("RequestType", requestType);
		parameterMap.put("DataSign", sign(businessParameter));
		parameterMap.put("DataType", "2");
		return JSONUtils.toJSONString(businessParameter);
	}

	/**
	 * 获取签名
	 * 
	 * @param requestData
	 * @param urlEncode
	 * @return
	 */
	public String sign(String requestData) {
		String md5 = SignUtils.md5UpperStr(requestData + apiKey, CHARSET_NAME);
		String base64;
		try {
			base64 = Base64.encode(md5.getBytes(CHARSET_NAME));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return HttpUtils.encode(base64, CHARSET_NAME);
	}

	public String doRequest(String requestUrl, String requestType, Map<String, Object> businessParameterMap) {
		String businessParameter = JSONUtils.toJSONString(businessParameterMap);
		Map<String, String> parameterMap = new HashMap<String, String>(8, 1);
		parameterMap.put("RequestData", businessParameter);
		parameterMap.put("EBusinessID", businessId);
		parameterMap.put("RequestType", requestType);
		parameterMap.put("DataSign", sign(businessParameter));
		parameterMap.put("DataType", "2");

		HttpRequest request = new BodyRequest(Method.POST, requestUrl,
				new ByteArray(JSONUtils.toJSONString(parameterMap), CHARSET_NAME));
		request.setContentType(new DefaultContentType(ContentType.APPLICATION_X_WWW_FORM_URLENCODED, CHARSET_NAME));
		ByteArray responseBody = NetworkUtils.execute(request);
		if (responseBody == null) {
			return null;
		}
		return responseBody.toString(CHARSET_NAME);
	}
}

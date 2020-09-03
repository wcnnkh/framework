package scw.kdniao;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.http.HttpUtils;
import scw.http.MediaType;
import scw.json.JSONUtils;
import scw.json.JsonObject;
import scw.net.uri.UriUtils;
import scw.security.SignType;
import scw.security.SignatureUtils;
import scw.util.ToMap;
import scw.util.XUtils;

/**
 * 快递鸟接口
 * 
 * @author shuchaowen
 *
 */
public class KDNiao {
	private static final String CHARSET_NAME = "UTF-8";
	private HashSet<String> dataSignUrlNotEncodeRequestTypeSet;
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

	public KDNiao(String businessId, String apiKey) {
		this(businessId, apiKey, false, true);
	}

	public synchronized void addDataSignUrlNotEncodeRequestTypeSet(String requestType) {
		if (dataSignUrlNotEncodeRequestTypeSet == null) {
			dataSignUrlNotEncodeRequestTypeSet = new HashSet<String>();
		}
		dataSignUrlNotEncodeRequestTypeSet.add(requestType);
	}

	public boolean dataSignIsUrlEncodeByRequestType(String requestType) {
		if ("101".equals(requestType) || "102".equals(requestType)) {
			return false;
		}

		if (dataSignUrlNotEncodeRequestTypeSet == null) {
			return true;
		}

		return !dataSignUrlNotEncodeRequestTypeSet.contains(requestType);
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

	/**
	 * 发送请求
	 * 
	 * @param requestUrl
	 * @param requestType
	 * @param businessParameterMap
	 * @return
	 */
	public String doRequest(String requestUrl, String requestType, Map<String, ?> businessParameterMap) {
		String requestData = JSONUtils.toJSONString(businessParameterMap);
		Map<String, String> parameterMap = new LinkedHashMap<String, String>(8, 1);
		parameterMap.put("RequestData", UriUtils.encode(requestData, CHARSET_NAME));
		parameterMap.put("EBusinessID", businessId);
		parameterMap.put("RequestType", requestType);
		String dataSign = SignatureUtils.sign(requestData + apiKey, CHARSET_NAME, SignType.MD5, SignType.BASE64);
		if (dataSignIsUrlEncodeByRequestType(requestType)) {
			dataSign = UriUtils.encode(dataSign, CHARSET_NAME);
		}
		parameterMap.put("DataSign", dataSign);
		parameterMap.put("DataType", "2");
		return HttpUtils.getHttpClient().post(String.class, requestUrl, parameterMap,
				new MediaType(MediaType.APPLICATION_JSON_VALUE, CHARSET_NAME)).getBody();
	}

	/**
	 * 即时查询
	 * 
	 * @param orderCode
	 *            订单编号(选填)
	 * @param shipperCode
	 *            快递公司编码
	 * @param logisticCode
	 *            物流单号
	 * @return
	 */
	public EbusinessOrderHandleResponse businessOrderHandle(String orderCode, String shipperCode, String logisticCode) {
		Map<String, String> map = new LinkedHashMap<String, String>(4, 1);
		map.put("OrderCode", orderCode);
		map.put("ShipperCode", shipperCode);
		map.put("LogisticCode", logisticCode);
		String url = "http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";
		if (isSandbox()) {
			url = "http://sandboxapi.kdniao.com:8080/kdniaosandbox/gateway/exterfaceInvoke.json";
		}

		String data = doRequest(url, "1002", map);
		if (data == null) {
			return null;
		}

		JsonObject json = JSONUtils.parseObject(data);
		if (json == null) {
			return null;
		}

		return new EbusinessOrderHandleResponse(json);
	}

	public String distRequest(String requestType, ToMap<String, Object> paramsMap) {
		return doRequest(isSandbox() ? "http://sandboxapi.kdniao.com:8080/kdniaosandbox/gateway/exterfaceInvoke.json"
				: "http://api.kdniao.com/api/dist", requestType, XUtils.toMap(paramsMap));
	}

	public SubscribeResponse subscribe(SubscribeRequestParameter parameter) {
		String content = distRequest("1008", parameter);
		if (content == null) {
			return null;
		}

		JsonObject json = JSONUtils.parseObject(content);
		if (json == null) {
			return null;
		}

		return new SubscribeResponse(json);
	}
}

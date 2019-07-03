package scw.utils.express.kdniao;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.core.Base64;
import scw.core.net.http.HttpUtils;
import scw.core.utils.SignUtils;
import scw.json.JSONObject;
import scw.json.JSONUtils;
import scw.utils.express.kdniao.response.EbusinessOrderHandleResponse;

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

	public KDNiao(String businessId, String apiKey, boolean sandbox,
			boolean https) {
		this.businessId = businessId;
		this.apiKey = apiKey;
		this.sandbox = sandbox;
		this.https = https;
	}

	public synchronized void addDataSignUrlNotEncodeRequestTypeSet(
			String requestType) {
		if (dataSignUrlNotEncodeRequestTypeSet == null) {
			dataSignUrlNotEncodeRequestTypeSet = new HashSet<String>();
		}
		dataSignUrlNotEncodeRequestTypeSet.add(requestType);
	}

	public boolean dataSignIsUrlEncodeByRequestType(String requestType) {
		if ("101".equals(requestType) || "102".equals(requestType)) {
			return false;
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
	public String doRequest(String requestUrl, String requestType,
			Map<String, ?> businessParameterMap) {
		String requestData = JSONUtils.toJSONString(businessParameterMap);
		Map<String, String> parameterMap = new LinkedHashMap<String, String>(8,
				1);
		parameterMap.put("RequestData",
				HttpUtils.encode(requestData, CHARSET_NAME));
		parameterMap.put("EBusinessID", businessId);
		parameterMap.put("RequestType", requestType);

		String dataSign = Base64.encode(SignUtils.md5Tobyte(requestData
				+ apiKey, CHARSET_NAME));
		if (dataSignIsUrlEncodeByRequestType(requestType)) {
			dataSign = HttpUtils.encode(dataSign, CHARSET_NAME);
		}

		parameterMap.put("DataSign", dataSign);
		parameterMap.put("DataType", "2");

		return HttpUtils.postForm(requestUrl, null, parameterMap, CHARSET_NAME);
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
	public EbusinessOrderHandleResponse businessOrderHandle(String orderCode,
			String shipperCode, String logisticCode) {
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

		JSONObject json = JSONUtils.parseObject(data);
		if (json == null) {
			return null;
		}

		return new EbusinessOrderHandleResponse(json);
	}
}

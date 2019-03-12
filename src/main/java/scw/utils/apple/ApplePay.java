package scw.utils.apple;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import scw.common.Logger;
import scw.common.utils.StringUtils;
import scw.net.http.HttpUtils;

public final class ApplePay {
	private static final String SANDBOX_URL = "https://sandbox.itunes.apple.com/verifyReceipt";
	private static final String DEV_RUL = "https://buy.itunes.apple.com/verifyReceipt";
	private final boolean shandbox;
	private final boolean debug;

	public ApplePay(boolean shandbox) {
		this(shandbox, true);
	}

	/**
	 * 是否沙盒模式
	 * 
	 * @param shandbox
	 */
	public ApplePay(boolean shandbox, boolean debug) {
		this.shandbox = shandbox;
		this.debug = debug;
	}

	private String getPayUrl() {
		return shandbox ? SANDBOX_URL : DEV_RUL;
	}

	/**
	 * @param receiptData
	 *            前端给的支付凭据 base64
	 * @param password
	 *            可选
	 * @param excludeOldTransactions
	 *            可选
	 * @return
	 */
	public JSONObject pay(String receiptData, String password, String excludeOldTransactions) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("receipt-data", receiptData);
		if (!StringUtils.isEmpty(password)) {
			map.put("password", password);
		}

		if (!StringUtils.isEmpty(excludeOldTransactions)) {
			map.put("exclude-old-transactions", excludeOldTransactions);
		}

		String response = HttpUtils.doPost(getPayUrl(), null, JSONObject.toJSONString(map));
		if (debug) {
			Logger.debug(this.getClass().getName(), response);
		}

		return JSONObject.parseObject(response);
	}

	public String getProductIdAndCheckReceipt(String receiptData, String password, String excludeOldTransactions) {
		JSONObject json = pay(receiptData, password, excludeOldTransactions);
		if (json.containsKey("status") && json.getIntValue("status") == 0) {
			json = json.getJSONObject("receipt");
			if (json.containsKey("in_app")) {// ios7
				JSONArray in_app = json.getJSONArray("in_app");
				if (in_app == null || in_app.size() == 0) {
					return null;
				}

				json = in_app.getJSONObject(0);
				if (json == null) {
					return null;
				}

				return json.getString("product_id");
			} else {
				return json.getString("product_id");
			}
		}
		return null;
	}
}

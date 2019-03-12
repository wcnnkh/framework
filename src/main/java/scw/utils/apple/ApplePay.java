package scw.utils.apple;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import scw.common.utils.StringUtils;
import scw.net.http.HttpUtils;

public final class ApplePay {
	private static final String SANDBOX_URL = "https://sandbox.itunes.apple.com/verifyReceipt";
	private static final String DEV_RUL = "https://buy.itunes.apple.com/verifyReceipt";
	private final boolean shandbox;

	/**
	 * 是否沙盒模式
	 * 
	 * @param shandbox
	 */
	public ApplePay(boolean shandbox) {
		this.shandbox = shandbox;
	}

	private String getPayUrl() {
		return shandbox ? SANDBOX_URL : DEV_RUL;
	}

	public String pay(String receiptData, String password, String excludeOldTransactions) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("receipt-data", receiptData);
		if (!StringUtils.isEmpty(password)) {
			map.put("password", password);
		}

		if (!StringUtils.isEmpty(excludeOldTransactions)) {
			map.put("exclude-old-transactions", excludeOldTransactions);
		}
		return HttpUtils.doPost(getPayUrl(), null, JSONObject.toJSONString(map));
	}
}

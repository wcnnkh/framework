package scw.utils.apple;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import scw.common.Logger;
import scw.common.utils.StringUtils;
import scw.net.http.HttpUtils;

public final class ApplePay {
	private static final String SANDBOX_URL = "https://sandbox.itunes.apple.com/verifyReceipt";
	private static final String DEV_URL = "https://buy.itunes.apple.com/verifyReceipt";
	private final boolean debug;

	public ApplePay() {
		this(true);
	}

	public ApplePay(boolean debug) {
		this.debug = debug;
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
	public ReceiptResponse checkReceipt(String host, String receiptData, String password,
			String excludeOldTransactions) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("receipt-data", receiptData);
		if (!StringUtils.isEmpty(password)) {
			map.put("password", password);
		}

		if (!StringUtils.isEmpty(excludeOldTransactions)) {
			map.put("exclude-old-transactions", excludeOldTransactions);
		}

		String response = HttpUtils.doPost(host, null, JSONObject.toJSONString(map));
		if (debug) {
			Logger.debug(this.getClass().getName(), response);
		}

		return JSONObject.parseObject(response, ReceiptResponse.class, Feature.SupportNonPublicField);
	}

	/**
	 * 自动检查是否是沙盒模式
	 * 
	 * @param receiptData
	 * @param password
	 * @param excludeOldTransactions
	 * @return
	 */
	public ReceiptResponse autoCheckReceipt(String receiptData, String password, String excludeOldTransactions) {
		ReceiptResponse res = checkReceipt(DEV_URL, receiptData, password, excludeOldTransactions);
		if (res.isOperationModeError()) {// 这是一个测试环境下的订单或者收据无法通过身份验证。
			res = checkReceipt(SANDBOX_URL, receiptData, password, excludeOldTransactions);
		}
		return res;
	}
}

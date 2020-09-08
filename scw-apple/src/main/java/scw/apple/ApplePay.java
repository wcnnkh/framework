package scw.apple;

import java.util.HashMap;
import java.util.Map;

import scw.core.utils.StringUtils;
import scw.http.HttpUtils;
import scw.http.MediaType;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class ApplePay {
	private static Logger logger = LoggerFactory.getLogger(ApplePay.class);
	static final String SANDBOX_URL = "https://sandbox.itunes.apple.com/verifyReceipt";
	static final String DEV_URL = "https://buy.itunes.apple.com/verifyReceipt";

	protected ReceiptResponse checkReceipt(String host, String receiptData, String password,
			String excludeOldTransactions) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("receipt-data", receiptData);
		if (!StringUtils.isEmpty(password)) {
			map.put("password", password);
		}

		if (!StringUtils.isEmpty(excludeOldTransactions)) {
			map.put("exclude-old-transactions", excludeOldTransactions);
		}

		String response = HttpUtils.getHttpClient().post(String.class, host, map, MediaType.APPLICATION_JSON).getBody();
		logger.debug(response);
		return JSONUtils.parseObject(response, ReceiptResponse.class);
	}

	/**
	 * 检查凭据(自动检查是否是沙盒模式)
	 * 
	 * @param receiptData
	 *            前端给的支付凭据 base64
	 * @param password
	 *            可选
	 * @param excludeOldTransactions
	 *            可选
	 * @return
	 */
	public ReceiptResponse checkReceipt(String receiptData, String password, String excludeOldTransactions) {
		ReceiptResponse res = checkReceipt(DEV_URL, receiptData, password, excludeOldTransactions);
		res.setSandbox(false);
		if (res.isOperationModeError()) {// 这是一个测试环境下的订单或者收据无法通过身份验证。
			res = checkReceipt(SANDBOX_URL, receiptData, password, excludeOldTransactions);
			res.setSandbox(true);
		}
		return res;
	}
}

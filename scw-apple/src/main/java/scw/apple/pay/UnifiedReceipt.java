package scw.apple.pay;

import java.util.List;

import scw.json.JSONUtils;
import scw.json.JsonObject;
import scw.json.JsonObjectWrapper;

/**
 * 包含有关应用程序最新应用内购买交易信息的对象。
 * {@link https://developer.apple.com/documentation/appstoreservernotifications/unified_receipt}
 * 
 * @author shuchaowen
 *
 */
public class UnifiedReceipt extends JsonObjectWrapper {

	public UnifiedReceipt(JsonObject target) {
		super(target);
	}

	/**
	 * 收据生成的环境。可能的值： Sandbox, Production
	 * 
	 * @return
	 */
	public String getEnvironment() {
		return getString("environment");
	}

	/**
	 * 最新的Base64编码的应用收据。
	 * 
	 * @return
	 */
	public String getLatestReceipt() {
		return getString("latest_receipt");
	}

	/**
	 * 包含的解码值最近的100次应用内购买交易的数组。该数组不包括您的应用已标记为完成的消耗品的交易
	 * 
	 * @return
	 */
	public List<LatestReceiptInfo> getLatestReceiptInfos() {
		return InApp.parse(getJsonArray("latest_receipt_info"), LatestReceiptInfo.class);
	}

	/**
	 * 一个数组，其中每个元素都包含中标识的每个自动续订的待定续订信息
	 * 
	 * @return
	 */
	public List<PendingRenewalInfo> getPendingRenewalInfos() {
		return JSONUtils.wrapper(getJsonArray("pending_renewal_info"), PendingRenewalInfo.class);
	}

	/**
	 * 状态码，其中0表示通知有效。值： 0
	 * 
	 * @return
	 */
	public int getStatus() {
		return getIntValue("status");
	}
}

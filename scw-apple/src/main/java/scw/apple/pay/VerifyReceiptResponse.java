package scw.apple.pay;

import java.util.List;

import scw.json.JsonObject;
import scw.json.JsonObjectWrapper;

/**
 * {@link https://developer.apple.com/documentation/appstorereceipts/responsebody}
 * 
 * @author shuchaowen
 *
 */
public class VerifyReceiptResponse extends JsonObjectWrapper {

	public VerifyReceiptResponse(JsonObject target) {
		super(target);
	}

	/**
	 * {@link https://developer.apple.com/documentation/appstorereceipts/status}
	 * 0 SUCCESS<br/>
	 * 21000 App Store不能读取你提供的JSON对象 <br/>
	 * 21002 receipt-data域的数据有问题<br/>
	 * 21003 receipt无法通过验证 <br/>
	 * 21004 提供的shared secret不匹配你账号中的shared secret<br/>
	 * 21005 receipt服务器当前不可用 <br/>
	 * 21006 receipt合法，但是订阅已过期。服务器接收到这个状态码时，receipt数据仍然会解码并一起发送 <br/>
	 * 21007 receipt是Sandbox receipt，但却发送至生产系统的验证服务<br/>
	 * 21008 receipt是生产receipt，但却发送至Sandbox环境的验证服务
	 * 
	 * @return
	 */
	public int getStatus() {
		return getIntValue("status");
	}

	/**
	 * 收据生成的环境。可能的值： Sandbox, Production
	 * 
	 * @return
	 */
	public String getEnvironment() {
		return getString("environment");
	}

	public boolean isSandbox() {
		return "Sandbox".equals(getEnvironment());
	}

	/**
	 * 发送用于验证的收据的JSON表示形式。
	 * 
	 * @return
	 */
	public Receipt getReceipt() {
		JsonObject jsonObject = getJsonObject("receipt");
		return jsonObject == null ? null : new Receipt(jsonObject);
	}

	/**
	 * 指示在请求期间发生错误的指示器。值1表示暂时性问题；稍后重试对此收据进行验证。值0表示无法解决的问题；请勿重试对此收据进行验证。仅适用于状态代码21100-
	 * 21199。
	 * 
	 * @return
	 */
	public boolean isRetryable() {
		return getBooleanValue("is_retryable");
	}

	/**
	 * 最新的Base64编码的应用程序收据。仅针对包含自动续订的收据返回。
	 * 
	 * @return
	 */
	public String getLatestReceipt() {
		return getString("latest_receipt");
	}

	/**
	 * 包含所有应用内购买交易的数组。这不包括已被您的应用标记为完成的消耗品交易。仅针对包含自动续订的收据返回。
	 * 
	 * @return
	 */
	public List<LatestReceiptInfo> getLatestReceiptInfos() {
		return LatestReceiptInfo.parse(getJsonArray("latest_receipt_info"));
	}

	/**
	 * 在JSON文件中，一个数组，其中每个元素包含由产品标识标识的每个自动续订订阅的挂起续订信息。仅对包含自动续订订阅的应用程序回执返回<br/>
	 * <br/>
	 * In the JSON file, an array where each element contains the pending
	 * renewal information for each auto-renewable subscription identified by
	 * the product_id. Only returned for app receipts that contain
	 * auto-renewable subscriptions.
	 * 
	 * @return
	 */
	public List<PendingRenewalInfo> getPendingRenewalInfos() {
		return PendingRenewalInfo.parse(getJsonArray("pending_renewal_info"));
	}

	public boolean isSuccess() {
		return getStatus() == 0;
	}

	public boolean isError() {
		return !isSuccess();
	}

	/**
	 * 运行环境错误 ，应该到沙盒模式或正式模式下尝试
	 * 
	 * @return
	 * @see #getStatus()
	 */
	public boolean isOperationModeError() {
		int status = getStatus();
		return status == 21007 || status == 21003;
	}
}

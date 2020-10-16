package scw.apple.pay;

import java.util.List;

import scw.json.JsonObject;
import scw.json.JsonObjectWrapper;

public class Notification extends JsonObjectWrapper {

	public Notification(JsonObject target) {
		super(target);
	}

	/**
	 * App Store Connect生成的标识符，App Store使用该标识符来唯一标识用户的订阅续订的自动续订订阅。将此值视为64位整数。
	 * 
	 * @return
	 */
	public long getAutoRenewAdamId() {
		return getLongValue("auto_renew_adam_id");
	}

	/**
	 * 用户的订阅续订的自动更新订阅的产品标识符。
	 * 
	 * @return
	 */
	public String getAutoRenewProductId() {
		return getString("auto_renew_product_id");
	}

	/**
	 * 自动续订订阅产品的当前续订状态。请注意，这些值与收据中的值不同。 1 订阅将在当前订阅期结束时续订。<br/>
	 * 0 客户已关闭订阅的自动续订。
	 * {@link https://developer.apple.com/documentation/appstorereceipts/auto_renew_status}
	 * 
	 * @return
	 */
	public boolean getAutoRenewStatus() {
		return getBooleanValue("auto_renew_status");
	}

	/**
	 * 自动续订订阅的续订状态处于打开或关闭状态的时间
	 * 
	 * @return
	 */
	public ApplePayDate getAutoRenewStatusChangeDate() {
		return new ApplePayDate(this, "auto_renew_status_change_date");
	}

	/**
	 * 收据生成的环境。可能的值： Sandbox, PROD
	 * 
	 * @return
	 */
	public String getEnvironment() {
		return getString("environment");
	}

	/**
	 * 订阅过期的原因。此字段仅在过期的自动续订中显示。
	 * 
	 * @see PendingRenewalInfo#getExpirationIntent()
	 * @return
	 */
	public int getExpirationIntent() {
		return getIntValue("expiration_intent");
	}

	/**
	 * 最新的Base64编码的交易收据。该字段显示在通知中，而不是过期的交易中。latest_receipt
	 * 
	 * @return
	 */
	public String getLatestExpiredReceipt() {
		return getString("latest_expired_receipt");
	}

	/**
	 * 此数组出现在通知中，而不是出现在过期的事务中。
	 * 
	 * @return
	 */
	public List<LatestReceiptInfo> getLatestExpiredReceiptInfos() {
		return InApp.parse(getJsonArray("latest_expired_receipt_info"), LatestReceiptInfo.class);
	}

	/**
	 * 最新的Base64编码的交易收据。
	 * 
	 * @return
	 */
	public String getLatestReceipt() {
		return getString("latest_receipt");
	}

	/**
	 * 请注意，此字段是收据中的数组，但服务器到服务器通知中是单个对象。latest_receipt
	 * 
	 * @return
	 */
	public LatestReceiptInfo getLatestReceiptInfo() {
		JsonObject jsonObject = getJsonObject("latest_receipt_info");
		return jsonObject == null ? null : new LatestReceiptInfo(jsonObject);
	}

	/**
	 * 触发通知的订阅事件。
	 * 
	 * @return
	 */
	public NotificationType getNotificationType() {
		return (NotificationType) getEnum("notification_type", NotificationType.class);
	}

	/**
	 * 触发通知的订阅事件。
	 * 
	 * @return
	 */
	public String getRawNotificationType() {
		return getString("notification_type");
	}

	/**
	 * 验证收据时password，与您在requestBody字段中提交的共享机密的值相同。
	 * 
	 * @return
	 */
	public String getPassword() {
		return getString("password");
	}

	/**
	 * 包含有关应用程序最新应用内购买交易信息的对象。
	 * 
	 * @return
	 */
	public UnifiedReceipt getUnifiedReceipt() {
		JsonObject jsonObject = getJsonObject("unified_receipt");
		return jsonObject == null ? null : new UnifiedReceipt(jsonObject);
	}

	/**
	 * 包含应用程序捆绑包ID的字符串。
	 * 
	 * @return
	 */
	public String getBid() {
		return getString("bid");
	}

	/**
	 * 包含应用程序捆绑包版本的字符串。
	 * 
	 * @return
	 */
	public String getBvrs() {
		return getString("bvrs");
	}
}

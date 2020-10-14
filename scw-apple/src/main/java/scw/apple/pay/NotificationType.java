package scw.apple.pay;

/**
 * 
 * 描述应用商店为其发送通知的应用内购买事件的类型。
 * {@link https://developer.apple.com/documentation/appstoreservernotifications/notification_type}
 * @author shuchaowen
 *
 */
public enum NotificationType {
	/**
	 * 表示Apple客户支持取消了订阅或用户升级了订阅。该密钥包含的变化的日期和时间。cancellation_date
	 */
	CANCEL,
	/**
	 * 指示客户对其订购计划进行了更改，该更改在下一次续订时生效。当前活动的计划不受影响。
	 */
	DID_CHANGE_RENEWAL_PREF,
	/**
	 * 指示订阅续订状态的更改。检查并在JSON响应中了解上次状态更新的日期和时间以及当前的续订状态。auto_renew_status_change_date_msauto_renew_status
	 */
	DID_CHANGE_RENEWAL_STATUS,
	/**
	 * 表示由于计费问题而无法续订的订阅。如果订阅处于计费宽限期内，请检查以了解订阅的当前重试状态，并了解新服务的到期日期。is_in_billing_retry_periodgrace_period_expires_date
	 */
	DID_FAIL_TO_RENEW,
	/**
	 * 表示成功的自动更新已过期的订阅，而该订阅过去无法更新。检查以确定下一个续订日期和时间。expires_date
	 */
	DID_RECOVER,
	/**
	 * 在用户最初购买订阅时发生。通过令牌存储在服务器上，以随时通过App Store验证用户的订阅状态。latest_receipt
	 */
	INITIAL_BUY,
	/**
	 * 指示客户使用您的应用程序界面或在该帐户的“订阅”设置中的App Store上以交互方式续订订阅。立即提供服务。
	 */
	INTERACTIVE_RENEWAL,
	/**
	 * 表示成功的自动更新已过期的订阅，而该订阅过去无法更新。检查以确定下一个续订日期和时间。expires_date
	 */
	RENEWAL,
	/**
	 * 表示App Store成功退还了一笔交易。该包含退回的交易的时间戳; 在和识别原始交易和产品，以及包含的原因。cancellation_date_msoriginal_transaction_idproduct_idcancellation_reason
	 */
	REFUND
}

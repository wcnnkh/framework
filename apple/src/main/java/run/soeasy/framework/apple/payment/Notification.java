package run.soeasy.framework.apple.payment;

import java.util.List;

import lombok.Data;

@Data
public class Notification {

	/**
	 * App Store Connect生成的标识符，App Store使用该标识符来唯一标识用户的订阅续订的自动续订订阅。将此值视为64位整数。
	 * 
	 */
	private Long autoRenewAdamId;

	/**
	 * 用户的订阅续订的自动更新订阅的产品标识符。
	 * 
	 */
	private String autoRenewProductId;

	/**
	 * 自动续订订阅产品的当前续订状态。
	 * 
	 * <a href=
	 * "https://developer.apple.com/documentation/appstorereceipts/auto_renew_status">文档</a>
	 * 
	 * 请注意，这些值与收据中的值不同。 1 订阅将在当前订阅期结束时续订。 0 客户已关闭订阅的自动续订。
	 */
	private Integer autoRenewStatus;

	/**
	 * 自动续订订阅的续订状态处于打开或关闭状态的时间
	 * 
	 */
	private String autoRenewStatusChangeDate;
	private Long autoRenewStatusChangeDateMs;
	private String autoRenewStatusChangeDatePst;

	/**
	 * 收据生成的环境。
	 * 
	 * 可能的值： Sandbox, PROD
	 */
	private String environment;

	/**
	 * 订阅过期的原因。
	 * 
	 * @see PendingRenewalInfo#getExpirationIntent() 此字段仅在过期的自动续订中显示。
	 */
	private Integer expirationIntent;

	/**
	 * 最新的Base64编码的交易收据。
	 * 
	 * 该字段显示在通知中，而不是过期的交易中。
	 */
	private String latestExpiredReceipt;

	/**
	 * 此数组出现在通知中，而不是出现在过期的事务中。
	 * 
	 * 此数组出现在通知中，而不是出现在过期的事务中
	 */
	private List<LatestReceiptInfo> latestExpiredReceiptInfos;

	/**
	 * 最新的Base64编码的交易收据。
	 * 
	 */
	private String latestReceipt;

	/**
	 * 请注意，此字段是收据中的数组，但服务器到服务器通知中是单个对象。latest_receipt
	 * 
	 */
	private LatestReceiptInfo latestReceiptInfo;

	/**
	 * 触发通知的订阅事件。
	 * 
	 */
	private String notificationType;

	/**
	 * 验证收据时password，与您在requestBody字段中提交的共享机密的值相同。
	 * 
	 */
	private String password;

	/**
	 * 包含有关应用程序最新应用内购买交易信息的对象。
	 * 
	 */
	private UnifiedReceipt unifiedReceipt;

	/**
	 * 包含应用程序捆绑包ID的字符串。
	 * 
	 */
	private String bid;

	/**
	 * 包含应用程序捆绑包版本的字符串。
	 * 
	 */
	private String bvrs;
}

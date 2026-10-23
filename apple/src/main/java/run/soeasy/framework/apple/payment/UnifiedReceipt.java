package run.soeasy.framework.apple.payment;

import java.util.List;

import lombok.Data;

/**
 * 包含有关应用程序最新应用内购买交易信息的对象。 <a href=
 * "https://developer.apple.com/documentation/appstoreservernotifications/unified_receipt">文档</a>
 * 
 * @author soeasy.run
 *
 */
@Data
public class UnifiedReceipt {
	/**
	 * 收据生成的环境。可能的值： Sandbox, Production
	 * 
	 */
	private String environment;

	/**
	 * 最新的Base64编码的应用收据。
	 * 
	 */
	private String latestReceipt;

	/**
	 * 包含的解码值最近的100次应用内购买交易的数组。该数组不包括您的应用已标记为完成的消耗品的交易
	 * 
	 */
	private List<LatestReceiptInfo> latestReceiptInfos;

	/**
	 * 一个数组，其中每个元素都包含中标识的每个自动续订的待定续订信息
	 */
	private List<PendingRenewalInfo> pendingRenewalInfos;

	/**
	 * 状态码，其中0表示通知有效。值： 0
	 */
	private Integer status;
}

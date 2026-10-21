package run.soeasy.framework.apple.payment;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LatestReceiptInfo extends InApp {

	/**
	 * 指示由于升级已取消订阅的指示器。该字段仅在升级交易中存在。值： true
	 * 
	 */
	private Boolean isUpgraded;

	/**
	 * 订阅所属的订阅组的标识符。该字段的值与SKProduct中的属性相同。
	 * 
	 */
	private String subscriptionGroupIdentifier;
}

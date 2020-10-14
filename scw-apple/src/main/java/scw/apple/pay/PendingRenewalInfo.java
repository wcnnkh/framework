package scw.apple.pay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.json.JsonObjectWrapper;

/**
 * {@link https://developer.apple.com/documentation/appstorereceipts/responsebody/pending_renewal_info}
 * 
 * @author shuchaowen
 *
 */
public class PendingRenewalInfo extends JsonObjectWrapper {

	public PendingRenewalInfo(JsonObject target) {
		super(target);
	}

	/**
	 * 自动续订订阅的当前续订首选项。该密钥的值对应于客户订阅续订的产品的属性。仅当用户降级或交叉降级到下一个订阅期的持续时间不同的订阅时，此字段才存在
	 * 
	 * @return
	 */
	public String getAutoRenewProductId() {
		return getString("auto_renew_product_id");
	}

	/**
	 * 自动续订的续订状态 1 订阅将在当前订阅期结束时续订。
	 * 
	 * 0 客户已关闭订阅的自动续订。
	 * 
	 * {@link https://developer.apple.com/documentation/appstorereceipts/auto_renew_status}
	 * 
	 * @return
	 */
	public int getAutoRenewStatus() {
		return getIntValue("auto_renew_status");
	}

	/**
	 * 订阅过期的原因<br/>
	 * 1 客户自愿取消订阅。<br/>
	 * 2 帐单错误；例如，客户的付款信息不再有效。<br/>
	 * 3 客户不同意最近的提价。<br/>
	 * 4 续订时无法购买该产品。<br/>
	 * 5 未知错误。
	 * {@link https://developer.apple.com/documentation/appstorereceipts/expiration_intent}
	 * 
	 * @return
	 */
	public int getExpirationIntent() {
		return getIntValue("expiration_intent");
	}

	/**
	 * 续订宽限期的到期时间
	 * 
	 * @return
	 */
	public ApplePayDate getGracePeriodExpiresDate() {
		return new ApplePayDate(this, "grace_period_expires_date");
	}

	/**
	 * 指示自动续订的订阅是否在计费重试期内。仅当自动续订的订阅处于计费重试状态时，此字段才存在 true App Store正在尝试续订。<br/>
	 * false App Store已停止尝试续订。
	 * {@link https://developer.apple.com/documentation/appstorereceipts/is_in_billing_retry_period}
	 * 
	 * @return
	 */
	public boolean isInBillingRetryPeriod() {
		return getBooleanValue("is_in_billing_retry_period");
	}

	/**
	 * 原始购买的交易标识符。
	 * 
	 * @return
	 */
	public String getOriginalTransactionId() {
		return getString("original_transaction_id");
	}

	/**
	 * 订阅价格上涨的价格同意状态。仅当通知客户提价后，才显示此字段。如果客户同意，默认值为，"0"并且更改为"1"。<br/>
	 * 可能的值： 1, 0
	 * 
	 * @return
	 */
	public int getPriceConsentStatus() {
		return getIntValue("price_consent_status");
	}

	/**
	 * 购买产品的唯一标识符。您可以在App Store Connect中创建产品时提供此值，它对应于存储在交易的付款属性中的对象的属性
	 * 
	 * @return
	 */
	public String getProductId() {
		return getString("product_id");
	}

	public static List<PendingRenewalInfo> parse(JsonArray jsonArray) {
		if (jsonArray == null) {
			return null;
		}

		if (jsonArray.isEmpty()) {
			return Collections.emptyList();
		}

		List<PendingRenewalInfo> list = new ArrayList<PendingRenewalInfo>();
		for (int i = 0; i < jsonArray.size(); i++) {
			list.add(new PendingRenewalInfo(jsonArray.getJsonObject(i)));
		}
		return list;
	}
}

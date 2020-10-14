package scw.apple.pay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.json.JsonObjectWrapper;
import scw.util.comparator.CompareUtils;

/**
 * 所述阵列是不按时间顺序。解析数组时，请遍历所有项以确保满足所有项。例如，您不能假定数组中的最后一项是最新的。<br/>
 * in_app对于包含自动续订订阅的收据，请检查响应的responseBody.Latest_receipt_info键的值以获取最新续订的状态。<br/>
 * 您可以使用此数组执行以下操作：<br/>
 * 检查有效收据中是否有空数组，表明App Store没有收取应用内购买费用。<br/>
 * 确定用户购买了哪些产品。非消耗性产品，自动更新订阅和非更新订阅的购买将无限期保留在收据中。对于消耗品，交易会在购买时添加到收据中，并一直保留到您的应用完成交易为止。致电后，它不再显示在更新的收据中。finishTransaction(_:)
 * {@link https://developer.apple.com/documentation/appstorereceipts/responsebody/receipt/in_app}
 * 
 * @author shuchaowen
 *
 */
public class InApp extends JsonObjectWrapper {
	public static final Comparator<InApp> COMPARATOR = new Comparator<InApp>() {

		public int compare(InApp o1, InApp o2) {
			return CompareUtils.compare(o1.getPurchaseDate().getMs(), o2.getPurchaseDate().getMs(), false);
		}
	};

	public InApp(JsonObject target) {
		super(target);
	}

	/**
	 * Apple客户支持取消交易的时间，或自动更新的订阅计划的升级时间。此字段仅用于退款交易。
	 * 
	 * @return
	 */
	public ApplePayDate getCancellationDate() {
		return new ApplePayDate(this, "cancellation_date");
	}

	/**
	 * 交易退款的原因。当客户取消交易时，App Store会给他们退款并为此密钥提供价值。<br/>
	 * 值“1”表示客户由于您的应用程序中存在实际或可感知的问题而取消了交易。<br/>
	 * 值“0”表示交易因其他原因被取消；例如，如果客户意外购买。<br/>
	 * 可能的值： 1, 0
	 * 
	 * @return
	 */
	public String getCancellationReason() {
		return getString("cancellation_reason");
	}

	/**
	 * 订阅到期的时间或续订的时间
	 * 
	 * @return
	 */
	public ApplePayDate getExpiresDate() {
		return new ApplePayDate(this, "expires_date");
	}

	/**
	 * 自动续订的订购是否在介绍价格期间
	 * 
	 * @return
	 */
	public boolean isInIntroOfferPeriod() {
		return getBooleanValue("is_in_intro_offer_period");
	}

	/**
	 * 指示订阅是否在免费试用期内
	 * 
	 * @return
	 */
	public boolean isTrialPeriod() {
		return getBooleanValue("is_trial_period");
	}

	/**
	 * 原始应用内购买的时间
	 * 
	 * @return
	 */
	public ApplePayDate getOriginalPurchaseDate() {
		return new ApplePayDate(this, "original_purchase_date");
	}

	/**
	 * 原始购买的交易标识符。请参阅以获取更多信息
	 * 
	 * @return
	 */
	public String getOriginalTransactionId() {
		return getString("original_transaction_id");
	}

	/**
	 * 购买产品的唯一标识符。您可以在App Store Connect中创建产品时提供此值，它对应于存储在交易的付款属性中的对象的属性
	 * 
	 * @return
	 */
	public String getProductId() {
		return getString("product_id");
	}

	/**
	 * 用户兑换的订阅报价的标识符。请参阅以获取更多信息
	 * 
	 * @return
	 */
	public String getPromotionalOfferId() {
		return getString("promotional_offer_id");
	}

	/**
	 * App Store在用户帐户中为购买或恢复的产品收取费用的时间，或者在过期后App Store对用户的帐户收取订阅购买或续费的时间
	 * 
	 * @return
	 */
	public ApplePayDate getPurchaseDate() {
		return new ApplePayDate(this, "purchase_date");
	}

	/**
	 * 购买的消费品数量。该值对应于SKPayment存储在交易的付款属性中的对象的数量属性。该值通常是“1”除非可以通过可变付款进行修改。最大值为10。
	 * 
	 * @return
	 */
	public int getQuantity() {
		return getIntValue("quantity");
	}

	/**
	 * 交易的唯一标识符，例如购买，还原或续订
	 * 
	 * @return
	 */
	public String getTransactionId() {
		return getString("transaction_id");
	}

	/**
	 * 跨设备购买事件（包括订阅更新事件）的唯一标识符。此值是识别订阅购买的主键。
	 * 
	 * @return
	 */
	public String getWebOrderLineItemId() {
		return getString("web_order_line_item_id");
	}

	public static List<InApp> parseApps(JsonArray jsonArray) {
		if (jsonArray == null) {
			return null;
		}

		if (jsonArray.isEmpty()) {
			return Collections.emptyList();
		}

		List<InApp> apps = new ArrayList<InApp>();
		for (int i = 0; i < jsonArray.size(); i++) {
			apps.add(new InApp(jsonArray.getJsonObject(i)));
		}
		apps.sort(COMPARATOR);
		return apps;
	}
}

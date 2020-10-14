package scw.apple.pay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scw.json.JsonArray;
import scw.json.JsonObject;

public class LatestReceiptInfo extends InApp {

	public LatestReceiptInfo(JsonObject target) {
		super(target);
	}

	/**
	 * 指示由于升级已取消订阅的指示器。该字段仅在升级交易中存在。值： true
	 * 
	 * @return
	 */
	public boolean isUpgraded() {
		return getBooleanValue("is_upgraded");
	}

	/**
	 * 订阅所属的订阅组的标识符。该字段的值与SKProduct中的属性相同。
	 * 
	 * @return
	 */
	public String getSubscriptionGroupIdentifier() {
		return getString("subscription_group_identifier");
	}

	public static List<LatestReceiptInfo> parse(JsonArray jsonArray) {
		if (jsonArray == null) {
			return null;
		}

		if (jsonArray.isEmpty()) {
			return Collections.emptyList();
		}

		List<LatestReceiptInfo> list = new ArrayList<LatestReceiptInfo>();
		for (int i = 0; i < jsonArray.size(); i++) {
			list.add(new LatestReceiptInfo(jsonArray.getJsonObject(i)));
		}
		list.sort(COMPARATOR);
		return list;
	}
}

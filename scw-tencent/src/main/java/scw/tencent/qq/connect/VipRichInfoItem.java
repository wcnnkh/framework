package scw.tencent.qq.connect;

import scw.json.JsonObject;
import scw.json.JsonObjectWrapper;

public class VipRichInfoItem extends JsonObjectWrapper {
	private final String prefix;

	public VipRichInfoItem(JsonObject target, String prefix) {
		super(target);
		this.prefix = prefix;
	}

	/**
	 * 最后一次充值时间
	 * 
	 * @return
	 */
	public long getStart() {
		return getLongValue(prefix + "_start");
	}

	/**
	 * 会员期限
	 * 
	 * @return
	 */
	public long getEnd() {
		return getLongValue(prefix + "_end");
	}

	/**
	 * 充值方式
	 * 
	 * @return
	 */
	public int getPayway() {
		return getIntValue(prefix + "_payway");
	}
}

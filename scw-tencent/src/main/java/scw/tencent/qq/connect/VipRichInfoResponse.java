package scw.tencent.qq.connect;

import scw.json.JsonObject;

public class VipRichInfoResponse extends QQResponse {

	public VipRichInfoResponse(JsonObject target) {
		super(target);
	}

	public boolean isVip() {
		return getBooleanValue("is_qq_vip");
	}

	public VipRichInfoItem getVip() {
		return new VipRichInfoItem(this, "qq_vip");
	}

	public VipRichInfoItem getYearVip() {
		return new VipRichInfoItem(this, "qq_year_vip");
	}

	/**
	 * QQ钻皇
	 * 
	 * @return
	 */
	public VipRichInfoItem getZuanHuang() {
		return new VipRichInfoItem(this, "qq_zuanhuang");
	}

	/**
	 * 豪华版QQ会员
	 * 
	 * @return
	 */
	public VipRichInfoItem getHaoHua() {
		return new VipRichInfoItem(this, "qq_haohua");
	}

	public VipRichInfoItem getSvip() {
		return new VipRichInfoItem(this, "qq_svip");
	}

	/**
	 * 非会员历史充值时间，仅在用户是非会员时信息有效
	 * 
	 * @return
	 */
	public long getHistoryPayTime() {
		return getLongValue("history_pay_time");
	}

	/**
	 * 非会员历史充值到期时间，仅在用户是非会员时信息有效
	 * 
	 * @return
	 */
	public long getHistoryEndTime() {
		return getLongValue("history_end_time");
	}
}

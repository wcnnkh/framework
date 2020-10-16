package scw.tencent.qq.connect;

import scw.json.JsonObject;

public class VipInfoResponse extends QQResponse {

	public VipInfoResponse(JsonObject target) {
		super(target);
	}

	/**
	 * 标识是否QQ会员
	 * 
	 * @return
	 */
	public boolean isVip() {
		return getBooleanValue("is_qq_vip");
	}

	/**
	 * 标识是否为年费QQ会员
	 * 
	 * @return
	 */
	public boolean isYearVip() {
		return getBooleanValue("is_qq_year_vip");
	}

	/**
	 * QQ会员等级信息
	 * 
	 * @return
	 */
	public int getVipLevel() {
		return getIntValue("qq_vip_level");
	}
}

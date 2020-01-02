package scw.tencent.wx;

import scw.json.JsonObject;

public final class WebUserInfo extends BaseResponse {
	private static final long serialVersionUID = 1L;
	private String openid;// 用户的唯一标识
	private String nickname;// 用户昵称
	private int sex;// 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
	private String province;// 用户个人资料填写的省份
	private String city;// 普通用户个人资料填写的城市
	private String country;// 国家，如中国为CN
	/**
	 * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像）， 用户没有头像时该项为空。
	 * 若用户更换头像，原有头像URL将失效。
	 */
	private String headimgurl;
	private String privilege;// 用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
	private String unionid;// 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。

	/**
	 * 用于序列化
	 */
	public WebUserInfo() {
		super(null);
	}

	public WebUserInfo(JsonObject json) {
		super(json);
		if (isSuccess()) {
			this.openid = json.getString("openid");
			this.nickname = json.getString("nickname");
			this.sex = json.getIntValue("sex");
			this.province = json.getString("province");
			this.city = json.getString("city");
			this.country = json.getString("country");
			this.headimgurl = json.getString("headimgurl");
			this.privilege = json.getString("privilege");
			this.unionid = json.getString("unionid");
		}
	}

	public String getOpenid() {
		return openid;
	}

	public String getNickname() {
		return nickname;
	}

	public int getSex() {
		return sex;
	}

	public String getProvince() {
		return province;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public String getPrivilege() {
		return privilege;
	}

	public String getUnionid() {
		return unionid;
	}
}

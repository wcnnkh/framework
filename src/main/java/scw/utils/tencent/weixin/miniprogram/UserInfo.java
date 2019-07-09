package scw.utils.tencent.weixin.miniprogram;

import scw.core.json.JSONObjectReadOnly;
import scw.core.json.JSONObjectReadOnlyWarpper;

/**
 * 未明确定义字段，因为在微信文档中说明返回内容字段可能增加
 * @author asus1
 *
 */
public class UserInfo extends JSONObjectReadOnlyWarpper {
	private static final long serialVersionUID = 1L;

	public UserInfo(JSONObjectReadOnly jsonObjectReadOnly) {
		super(jsonObjectReadOnly);
	}

	public String getOpenId() {
		return getString("openId");
	}

	public String getNickName() {
		return getString("nickName");
	}

	public String getGender() {
		return getString("gender");
	}

	public String getCity() {
		return getString("city");
	}

	public String getProvince() {
		return getString("province");
	}

	public String getCountry() {
		return getString("country");
	}

	public String getAvatarUrl() {
		return getString("avatarUrl");
	}

	public String getUnionId() {
		return getString("unionId");
	}

	public WaterMark getWaterMark() {
		return getObject("watermark", WaterMark.class);
	}

	public static class WaterMark extends JSONObjectReadOnlyWarpper {
		private static final long serialVersionUID = 1L;

		public WaterMark(JSONObjectReadOnly jsonObjectReadOnly) {
			super(jsonObjectReadOnly);
		}

		public String getAppid() {
			return getString("appid");
		}

		public long getTimestamp() {
			return getLongValue("timestamp");
		}
	}
}

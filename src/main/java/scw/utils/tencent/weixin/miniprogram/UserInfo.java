package scw.utils.tencent.weixin.miniprogram;

import scw.core.json.JSONObject;
import scw.core.json.JSONObjectReadOnly;
import scw.core.json.JSONObjectReadOnlyWarpper;
import scw.core.json.JSONUtils;

/**
 * 未明确定义字段，因为在微信文档中说明返回内容字段可能增加
 * 
 * @author asus1
 *
 */
public class UserInfo extends JSONObjectReadOnlyWarpper {
	private static final long serialVersionUID = 1L;

	public UserInfo(String encryptedData, String sessionKey, String iv) {
		this(JSONUtils.parseObject(WeiXinMiniprogramUtils.decrypt(encryptedData, sessionKey, iv)));
	}

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
		JSONObject json = getJSONObject("watermark");
		return json == null ? null : new WaterMark(json);
	}
}

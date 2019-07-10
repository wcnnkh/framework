package scw.utils.tencent.weixin.miniprogram;

import java.io.Serializable;

import scw.core.json.JSONObject;
import scw.core.json.JSONObjectReadOnly;
import scw.core.json.JSONUtils;

public class PhoneNumber implements Serializable {
	private static final long serialVersionUID = 1L;
	private String phoneNumber;
	private String purePhoneNumber;
	private String countryCode;
	private WaterMark waterMark;

	PhoneNumber() {
	}

	public PhoneNumber(String encryptedData, String sessionKey, String iv) {
		this(JSONUtils.parseObject(WeiXinMiniprogramUtils.decrypt(encryptedData, sessionKey, iv)));
	}

	public PhoneNumber(JSONObjectReadOnly jsonObjectReadOnly) {
		if (jsonObjectReadOnly != null) {
			this.phoneNumber = jsonObjectReadOnly.getString("phoneNumber");
			this.purePhoneNumber = jsonObjectReadOnly.getString("purePhoneNumber");
			this.countryCode = jsonObjectReadOnly.getString("countryCode");
			JSONObject jsonObject = jsonObjectReadOnly.getJSONObject("watermark");
			if (jsonObject != null) {
				this.waterMark = new WaterMark(jsonObject);
			}
		}
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getPurePhoneNumber() {
		return purePhoneNumber;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public WaterMark getWaterMark() {
		return waterMark;
	}
}

package scw.utils.tencent.weixin.miniprogram;

import java.io.Serializable;

import scw.core.json.JSONObjectReadOnly;

public class WaterMark implements Serializable {
	private static final long serialVersionUID = 1L;
	private String appid;
	private long timestamp;

	WaterMark() {
	}

	public WaterMark(JSONObjectReadOnly jsonObjectReadOnly) {
		if (jsonObjectReadOnly != null) {
			this.appid = jsonObjectReadOnly.getString("appid");
			this.timestamp = jsonObjectReadOnly.getLongValue("timestamp");
		}
	}

	public String getAppid() {
		return appid;
	}

	public long getTimestamp() {
		return timestamp;
	}
}

package scw.tencent.wx.miniprogram;

import java.io.Serializable;

import scw.json.JsonObject;

public class WaterMark implements Serializable {
	private static final long serialVersionUID = 1L;
	private String appid;
	private long timestamp;

	WaterMark() {
	}

	public WaterMark(JsonObject jsonObject) {
		if (jsonObject != null) {
			this.appid = jsonObject.getString("appid");
			this.timestamp = jsonObject.getLongValue("timestamp");
		}
	}

	public String getAppid() {
		return appid;
	}

	public long getTimestamp() {
		return timestamp;
	}
}

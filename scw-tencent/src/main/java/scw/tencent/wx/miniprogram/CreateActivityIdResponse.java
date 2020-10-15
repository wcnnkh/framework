package scw.tencent.wx.miniprogram;

import scw.json.JsonObject;

public final class CreateActivityIdResponse extends BaseResponse {

	public CreateActivityIdResponse(JsonObject json) {
		super(json);
	}

	public String getActivityId() {
		return getString("activity_id");
	}

	public long getExpirationTime() {
		return getLongValue("expiration_time");
	}
}

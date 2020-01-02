package scw.tencent.wx.miniprogram;

import scw.json.JsonObject;
import scw.tencent.wx.BaseResponse;

public final class CreateActivityIdResponse extends BaseResponse {
	private static final long serialVersionUID = 1L;
	private String activity_id;
	private Long expiration_time;

	public CreateActivityIdResponse(JsonObject json) {
		super(json);
		if (isSuccess()) {
			this.activity_id = json.getString("activity_id");
			this.expiration_time = json.getLong("expiration_time");
		}
	}

	public String getActivity_id() {
		return activity_id;
	}

	public Long getExpiration_time() {
		return expiration_time;
	}
}

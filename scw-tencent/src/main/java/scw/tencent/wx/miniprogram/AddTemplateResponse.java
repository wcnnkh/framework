package scw.tencent.wx.miniprogram;

import scw.json.JsonObject;

public final class AddTemplateResponse extends BaseResponse {

	public AddTemplateResponse(JsonObject json) {
		super(json);
	}

	public String getTemplateId() {
		return getString("template_id");
	}
}
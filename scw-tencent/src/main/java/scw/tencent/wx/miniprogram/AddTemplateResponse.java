package scw.tencent.wx.miniprogram;

import scw.json.JsonObject;
import scw.tencent.wx.BaseResponse;

public final class AddTemplateResponse extends BaseResponse {
	private static final long serialVersionUID = 1L;
	private String template_id;

	public AddTemplateResponse(JsonObject json) {
		super(json);
		if (isSuccess()) {
			this.template_id = json.getString("template_id");
		}
	}

	public String getTemplate_id() {
		return template_id;
	}
}
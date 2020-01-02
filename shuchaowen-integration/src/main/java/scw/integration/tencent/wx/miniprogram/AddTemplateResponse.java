package scw.integration.tencent.wx.miniprogram;

import scw.integration.tencent.wx.BaseResponse;
import scw.json.JsonObject;

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
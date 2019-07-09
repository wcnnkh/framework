package scw.utils.tencent.weixin.miniprogram;

import scw.core.json.JSONObject;
import scw.utils.tencent.weixin.BaseResponse;

public final class AddTemplateResponse extends BaseResponse {
	private static final long serialVersionUID = 1L;
	private String template_id;

	public AddTemplateResponse(JSONObject json) {
		super(json);
		if (isSuccess()) {
			this.template_id = json.getString("template_id");
		}
	}

	public String getTemplate_id() {
		return template_id;
	}
}
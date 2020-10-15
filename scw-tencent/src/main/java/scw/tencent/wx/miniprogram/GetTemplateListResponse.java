package scw.tencent.wx.miniprogram;

import java.util.List;

import scw.json.JSONUtils;
import scw.json.JsonObject;

public final class GetTemplateListResponse extends BaseResponse {

	GetTemplateListResponse() {
		super(null);
	}

	public GetTemplateListResponse(JsonObject json) {
		super(json);
	}

	public List<Template> getList() {
		return JSONUtils.parseArray(getJsonArray("list"), Template.class);
	}
}

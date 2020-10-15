package scw.tencent.wx.miniprogram;

import java.util.List;

import scw.json.JSONUtils;
import scw.json.JsonObject;

public final class GetTemplateLibraryListResponse extends BaseResponse {

	GetTemplateLibraryListResponse(JsonObject json) {
		super(json);
	}

	public List<TemplateLibrary> getList() {
		return JSONUtils.parseArray(getJsonArray("list"), TemplateLibrary.class);
	}

	public int getTotal_count() {
		return getIntValue("total_count");
	}
}

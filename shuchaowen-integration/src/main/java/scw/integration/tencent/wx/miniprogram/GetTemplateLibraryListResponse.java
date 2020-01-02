package scw.integration.tencent.wx.miniprogram;

import java.util.ArrayList;
import java.util.List;

import scw.integration.tencent.wx.BaseResponse;
import scw.json.JsonArray;
import scw.json.JsonObject;

public final class GetTemplateLibraryListResponse extends BaseResponse {
	private static final long serialVersionUID = 1L;
	private List<TemplateLibrary> list;
	private int total_count;// 模板库标题总数

	GetTemplateLibraryListResponse() {
		super(null);
	}

	GetTemplateLibraryListResponse(JsonObject json) {
		super(json);
		if (isSuccess()) {
			this.total_count = json.getIntValue("total_count");
			JsonArray jsonArray = json.getJsonArray("list");
			if (jsonArray != null) {
				this.list = new ArrayList<TemplateLibrary>();
				for (int i = 0; i < jsonArray.size(); i++) {
					list.add(jsonArray.getObject(i, TemplateLibrary.class));
				}
			}
		}
	}

	public List<TemplateLibrary> getList() {
		return list;
	}

	public int getTotal_count() {
		return total_count;
	}
}

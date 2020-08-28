package scw.tencent.wx.miniprogram;

import java.util.ArrayList;
import java.util.List;

import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.tencent.wx.BaseResponse;

public final class GetTemplateListResponse extends BaseResponse {
	private static final long serialVersionUID = 1L;
	private List<Template> list;

	GetTemplateListResponse() {
		super(null);
	}

	public GetTemplateListResponse(JsonObject json) {
		super(json);
		if (isSuccess()) {
			JsonArray jsonArray = json.getJsonArray("list");
			if (jsonArray != null) {
				this.list = new ArrayList<Template>();
				for (int i = 0; i < jsonArray.size(); i++) {
					list.add(jsonArray.getObject(i, Template.class));
				}
			}
		}
	}

	public List<Template> getList() {
		return list;
	}
}

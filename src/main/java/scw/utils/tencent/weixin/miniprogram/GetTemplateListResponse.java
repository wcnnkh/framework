package scw.utils.tencent.weixin.miniprogram;

import java.util.ArrayList;
import java.util.List;

import scw.core.json.JSONArray;
import scw.core.json.JSONObject;
import scw.utils.tencent.weixin.BaseResponse;

public final class GetTemplateListResponse extends BaseResponse {
	private static final long serialVersionUID = 1L;
	private List<Template> list;

	GetTemplateListResponse() {
		super(null);
	}

	public GetTemplateListResponse(JSONObject json) {
		super(json);
		if (isSuccess()) {
			JSONArray jsonArray = json.getJSONArray("list");
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

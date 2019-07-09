package scw.utils.tencent.weixin.miniprogram;

import java.util.ArrayList;
import java.util.List;

import scw.core.json.JSONArray;
import scw.core.json.JSONObject;
import scw.utils.tencent.weixin.BaseResponse;

public final class GetTemplateLibraryByIdResponse extends BaseResponse {
	private static final long serialVersionUID = 1L;
	private String id;// 模板标题 id
	private String title;// 模板标题
	private List<Keyword> keyword_list;// 关键词列表
	
	GetTemplateLibraryByIdResponse() {
		super(null);
	}

	public GetTemplateLibraryByIdResponse(JSONObject json) {
		super(json);
		if (isSuccess()) {
			this.id = json.getString("id");
			this.title = json.getString("title");
			JSONArray jsonArray = json.getJSONArray("keyword_list");
			if (jsonArray != null) {
				this.keyword_list = new ArrayList<Keyword>();
				for (int i = 0; i < jsonArray.size(); i++) {
					keyword_list.add(jsonArray.getObject(i, Keyword.class));
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public List<Keyword> getKeyword_list() {
		return keyword_list;
	}
}

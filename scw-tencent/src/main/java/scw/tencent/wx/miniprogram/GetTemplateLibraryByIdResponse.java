package scw.tencent.wx.miniprogram;

import java.util.List;

import scw.json.JsonObject;

public final class GetTemplateLibraryByIdResponse extends BaseResponse {

	public GetTemplateLibraryByIdResponse(JsonObject json) {
		super(json);
	}

	/**
	 * 模板标题 id
	 * 
	 * @return
	 */
	public String getId() {
		return getString("id");
	}

	/**
	 * 模板标题
	 * 
	 * @return
	 */
	public String getTitle() {
		return getString("title");
	}

	/**
	 * 关键词列表
	 * 
	 * @return
	 */
	public List<Keyword> getKeywordList() {
		return Keyword.parse(getJsonArray("keyword_list"));
	}
}

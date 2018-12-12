package shuchaowen.tencent.weixin.miniprogram.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import shuchaowen.tencent.weixin.WeiXinProcess;
import shuchaowen.tencent.weixin.miniprogram.bean.Keyword;

/**
 * 获取模板库某个模板标题下关键词库
 * @author shuchaowen
 *
 */
public final class GetTemplateLibraryById extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/wxopen/template/library/get?access_token=";
	private String id;//模板标题 id
	private String title;//	模板标题
	private List<Keyword> keyword_list;//关键词列表
	
	/**
	 * @param access_token
	 * @param id 模板标题id，可通过接口获取，也可登录小程序后台查看获取
	 */
	public GetTemplateLibraryById(String access_token, String id){
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		JSONObject json = post(API + access_token, map);
		if(isSuccess()){
			this.id = json.getString("id");
			this.title = json.getString("title");
			JSONArray jsonArray = json.getJSONArray("keyword_list");
			if(jsonArray != null){
				this.keyword_list = new ArrayList<Keyword>();
				for(int i=0; i<jsonArray.size(); i++){
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

package scw.utils.tencent.weixin.miniprogram.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.core.json.JSONArray;
import scw.core.json.JSONObject;
import scw.utils.tencent.weixin.WeiXinProcess;
import scw.utils.tencent.weixin.miniprogram.bean.Template;

/**
 * 获取帐号下已存在的模板列表
 * @author shuchaowen
 *
 */
public final class GetTemplateList extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/wxopen/template/list?access_token=";
	private List<Template> list;
	
	/**
	 * 
	 * @param access_token
	 * @param offset 用于分页，表示从offset开始。从 0 开始计数。
	 * @param count 用于分页，表示拉取count条记录。最大为 20。最后一页的list长度可能小于请求的count。
	 */
	public GetTemplateList(String access_token, int offset, int count){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", offset);
		map.put("count", count);
		JSONObject json = post(API + access_token, map);
		if(isSuccess()){
			JSONArray jsonArray = json.getJSONArray("list");
			if(jsonArray != null){
				this.list = new ArrayList<Template>();
				for(int i=0; i<jsonArray.size(); i++){
					list.add(jsonArray.getObject(i, Template.class));
				}
			}
		}
	}

	public List<Template> getList() {
		return list;
	}
}

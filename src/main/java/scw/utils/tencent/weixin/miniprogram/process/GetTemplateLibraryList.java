package scw.utils.tencent.weixin.miniprogram.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.json.JSONArray;
import scw.json.JSONObject;
import scw.utils.tencent.weixin.WeiXinProcess;
import scw.utils.tencent.weixin.miniprogram.bean.TemplateLibrary;

/**
 * 获取小程序模板库标题列表
 * @author shuchaowen
 *
 */
public final class GetTemplateLibraryList extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/wxopen/template/library/list?access_token=";
	private List<TemplateLibrary> list;
	private int total_count;//模板库标题总数
	
	/**
	 * @param access_token
	 * @param offset 用于分页，表示从offset开始。从 0 开始计数。
	 * @param count 用于分页，表示拉取count条记录。最大为 20。
	 */
	public GetTemplateLibraryList(String access_token, int offset, int count){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", offset);
		map.put("count", count);
		JSONObject json = post(API + access_token, map);
		if(isSuccess()){
			this.total_count = json.getIntValue("total_count");
			JSONArray jsonArray = json.getJSONArray("list");
			if(jsonArray != null){
				this.list = new ArrayList<TemplateLibrary>();
				for(int i=0; i<jsonArray.size(); i++){
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

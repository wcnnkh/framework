package shuchaowen.tencent.weixin.miniprogram.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import shuchaowen.tencent.weixin.WeiXinProcess;
import shuchaowen.tencent.weixin.miniprogram.process.bean.TemplateLibrary;

public final class GetTemplateLibraryList extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/wxopen/template/library/list?access_token=";
	private List<TemplateLibrary> list;
	private int total_count;//模板库标题总数
	
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

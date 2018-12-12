package shuchaowen.tencent.weixin.miniprogram.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import shuchaowen.tencent.weixin.WeiXinProcess;
import shuchaowen.tencent.weixin.miniprogram.process.bean.Template;

public final class GetTemplateList extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/wxopen/template/list?access_token=";
	private List<Template> list;
	
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

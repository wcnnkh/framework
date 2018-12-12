package shuchaowen.tencent.weixin.miniprogram.process;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import shuchaowen.tencent.weixin.WeiXinProcess;
import shuchaowen.tencent.weixin.miniprogram.process.enums.TargetState;
import shuchaowen.tencent.weixin.miniprogram.process.enums.TemplateParameterName;

public final class SetUpdatableMsg extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/message/wxopen/updatablemsg/send?access_token=";
	
	public SetUpdatableMsg(String access_token, String activity_id, TargetState target_state, EnumMap<TemplateParameterName, String> parameter_list){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("activity_id", activity_id);
		map.put("target_state", target_state.getState());
		
		Map<String, Object> template_info = new HashMap<String, Object>(2);
		JSONArray jsonArray = new JSONArray();
		for(Entry<TemplateParameterName, String> entry : parameter_list.entrySet()){
			JSONObject json = new JSONObject(4);
			json.put("name", entry.getKey().name());
			json.put("value", entry.getValue());
			jsonArray.add(json);
		}
		template_info.put("parameter_list", jsonArray);
		map.put("template_info", template_info);
		post(API + access_token, map);
	}
}

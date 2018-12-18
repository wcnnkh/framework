package scw.tencent.weixin.miniprogram.process;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import scw.tencent.weixin.WeiXinProcess;
import scw.tencent.weixin.miniprogram.enums.TargetState;
import scw.tencent.weixin.miniprogram.enums.TemplateParameterName;

/**
 * 修改被分享的动态消息
 * @author shuchaowen
 *
 */
public final class SetUpdatableMsg extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/message/wxopen/updatablemsg/send?access_token=";
	
	/**
	 * @param access_token
	 * @param activity_id 动态消息的 ID，通过 createActivityId 接口获取
	 * @param target_state 动态消息修改后的状态（具体含义见后文）
	 * @param parameter_list 动态消息对应的模板信息
	 */
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

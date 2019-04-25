package scw.servlet.view.common;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import scw.core.utils.StringUtils;

public class MapResult extends DataResult<Map<String, Object>>{
	private static final long serialVersionUID = 1L;
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	
	@Override
	public Map<String, Object> getData() {
		return dataMap;
	}
	
	public MapResult put(String key, Object value){
		dataMap.put(key, value);
		return this;
	}
	
	@Override
	public String getResponseText() {
		JSONObject json = new JSONObject(4);
		String msg = getMsg();
		json.put("code", StringUtils.isEmpty(msg)? 0:getCode());
		json.put("msg", msg);
		json.put("data", dataMap);
		return json.toJSONString();
	}
}

package scw.json.support.fastjson;

import com.alibaba.fastjson.JSON;

import scw.json.JSONObject;
import scw.json.JSONParseSupport;

public final class FastJSONParseSupport implements JSONParseSupport {

	public scw.json.JSONArray parseArray(String text) {
		return new FastJSONArray(com.alibaba.fastjson.JSONArray.parseArray(text));
	}

	public JSONObject parseObject(String text) {
		return new FastJSONObject(com.alibaba.fastjson.JSONObject.parseObject(text));
	}

	public String toJSONString(Object obj) {
		return JSON.toJSONString(obj);
	}

	public <T> T parseObject(String text, Class<T> type) {
		return JSON.parseObject(text, type);
	}

}

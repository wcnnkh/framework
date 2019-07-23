package scw.core.json.support.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import scw.core.json.JSONObject;
import scw.core.json.JSONParseSupport;

public final class FastJSONParseSupport implements JSONParseSupport {

	public scw.core.json.JSONArray parseArray(String text) {
		JSONArray jArray = com.alibaba.fastjson.JSONArray.parseArray(text);
		return jArray == null ? null : new FastJSONArrayWrapper(jArray);
	}

	public JSONObject parseObject(String text) {
		com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(text);
		return jsonObject == null ? null : new FastJSONObjectWrapper(jsonObject);
	}

	public String toJSONString(Object obj) {
		return JSON.toJSONString(obj, BaseProperyFilter.BASE_PROPERY_FILTER);
	}

	public <T> T parseObject(String text, Class<T> type) {
		return JSON.parseObject(text, type);
	}

	public scw.core.json.JSONArray createJSONArray() {
		return new FastJSONArrayWrapper(new JSONArray());
	}

	public JSONObject createJSONObject() {
		return new FastJSONObjectWrapper(new com.alibaba.fastjson.JSONObject());
	}
}

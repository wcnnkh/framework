package scw.json.support;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.Feature;

import scw.json.JsonObject;
import scw.json.JSONSupport;

public final class FastJsonSupport implements JSONSupport {

	public scw.json.JsonArray parseArray(String text) {
		JSONArray jArray = com.alibaba.fastjson.JSONArray.parseArray(text);
		return jArray == null ? null : new FastJsonArray(jArray);
	}

	public JsonObject parseObject(String text) {
		com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(text);
		return jsonObject == null ? null : new FastJsonObject(jsonObject);
	}

	public String toJSONString(Object obj) {
		return JSON.toJSONString(obj, FastJSONBaseProperyFilter.BASE_PROPERY_FILTER);
	}

	public <T> T parseObject(String text, Class<T> type) {
		return JSON.parseObject(text, type, Feature.SupportNonPublicField);
	}

	public <T> T parseObject(String text, Type type) {
		return JSON.parseObject(text, type, Feature.SupportNonPublicField);
	}
}

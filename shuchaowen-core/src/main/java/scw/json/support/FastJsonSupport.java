package scw.json.support;

import java.lang.reflect.Type;

import scw.json.AbstractJSONSupport;
import scw.json.JsonObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.Feature;

public final class FastJsonSupport extends AbstractJSONSupport {

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

	@Override
	protected <T> T parseObjectInternal(String text, Class<T> type) {
		return JSON.parseObject(text, type, Feature.SupportNonPublicField);
	}

	@Override
	protected <T> T parseObjectInternal(String text, Type type) {
		return JSON.parseObject(text, type, Feature.SupportNonPublicField);
	}
}

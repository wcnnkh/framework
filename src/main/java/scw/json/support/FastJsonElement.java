package scw.json.support;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.parser.Feature;

import scw.json.AbstractJsonElement;
import scw.json.JsonArray;
import scw.json.JsonObject;

public final class FastJsonElement extends AbstractJsonElement implements JSONAware {
	private String text;

	public FastJsonElement(String text) {
		this.text = text;
	}

	@Override
	protected <T> T getAsObjectNotSupport(Class<? extends T> type) {
		return JSON.parseObject(text, type, Feature.SupportNonPublicField);
	}

	@Override
	protected <T> T getAsObjectNotSupport(Type type) {
		return JSON.parseObject(text, type, Feature.SupportNonPublicField);
	}

	public JsonArray getAsJsonArray() {
		return new FastJsonArray(com.alibaba.fastjson.JSONArray.parseArray(text));
	}

	public JsonObject getAsJsonObject() {
		return new FastJsonObject(com.alibaba.fastjson.JSONObject.parseObject(text));
	}

	public String getAsString() {
		return text;
	}

	public String toJSONString() {
		return text;
	}
}

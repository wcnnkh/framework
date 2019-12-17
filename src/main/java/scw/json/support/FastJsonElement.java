package scw.json.support;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;

import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.json.JsonElement;
import scw.util.AbstractValue;

public class FastJsonElement extends AbstractValue implements JsonElement {
	private String text;

	public FastJsonElement(String text) {
		this.text = text;
	}

	@Override
	protected <T> T notSupportParse(Class<? extends T> type) {
		return JSON.parseObject(text, type, Feature.SupportNonPublicField);
	}

	@Override
	protected <T> T notSupportParse(Type type) {
		return JSON.parseObject(text, type, Feature.SupportNonPublicField);
	}

	public String parseString() {
		return text;
	}

	public JsonArray parseJsonArray() {
		return new FastJsonArray(com.alibaba.fastjson.JSONArray.parseArray(text));
	}

	public JsonObject parseJsonObject() {
		return new FastJsonObject(com.alibaba.fastjson.JSONObject.parseObject(text));
	}
}

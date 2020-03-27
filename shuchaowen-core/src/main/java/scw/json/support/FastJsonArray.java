package scw.json.support;

import java.lang.reflect.Type;
import java.util.Iterator;

import scw.core.Converter;
import scw.core.utils.IteratorConvert;
import scw.json.JsonArray;
import scw.json.JsonElement;
import scw.json.JsonObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONAware;

public final class FastJsonArray extends JsonArray implements JSONAware {
	private JSONArray jsonArray;

	public FastJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public Iterator<JsonElement> iterator() {
		return new IteratorConvert<Object, JsonElement>(jsonArray.iterator(), new Converter<Object, JsonElement>() {

			public JsonElement convert(Object k) throws Exception {
				if (k == null) {
					return null;
				}

				if (k instanceof JsonElement) {
					return (JsonElement) k;
				}
				return new FastJsonElement(k.toString(), getDefaultValue(null));
			}
		});
	}

	public JsonElement get(Integer index) {
		String text = jsonArray.getString(index);
		return text == null ? null : new FastJsonElement(text, getDefaultValue(index));
	}

	public JsonObject getJsonObject(int index) {
		com.alibaba.fastjson.JSONObject json = jsonArray.getJSONObject(index);
		return json == null ? null : new FastJsonObject(json);
	}

	public scw.json.JsonArray getJsonArray(int index) {
		JSONArray jarr = jsonArray.getJSONArray(index);
		return jarr == null ? null : new FastJsonArray(jarr);
	}

	public void add(Object value) {
		jsonArray.add(value);
	}

	public int size() {
		return jsonArray.size();
	}

	public String toJsonString() {
		return JSON.toJSONString(jsonArray, FastJSONBaseProperyFilter.BASE_PROPERY_FILTER);
	}

	public <T> T getObject(int index, Class<? extends T> type) {
		return jsonArray.getObject(index, type);
	}

	public Object getObject(int index, Type type) {
		return jsonArray.getObject(index, type);
	}

	public String getString(int index) {
		return jsonArray.getString(index);
	}

	public String toJSONString() {
		return toJsonString();
	}
}

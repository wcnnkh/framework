package scw.json.support;

import java.lang.reflect.Type;
import java.util.Iterator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import scw.core.Converter;
import scw.core.utils.IteratorConvert;
import scw.json.JsonObject;
import scw.json.JsonElement;

public class FastJsonArray implements scw.json.JsonArray {
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
				return new FastJsonElement(k.toString());
			}
		});
	}

	public JsonElement get(int index) {
		String text = jsonArray.getString(index);
		return text == null ? null : new FastJsonElement(text);
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

	@Override
	public String toString() {
		return toJsonString();
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

	public Byte getByte(int index) {
		JsonElement jsonElement = get(index);
		return jsonElement == null ? null : jsonElement.parseByte();
	}
}

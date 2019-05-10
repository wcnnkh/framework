package scw.json.support;

import java.util.Set;

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

}

final class FastJSONObject implements scw.json.JSONObject {
	private final com.alibaba.fastjson.JSONObject json;

	public FastJSONObject(com.alibaba.fastjson.JSONObject json) {
		this.json = json;
	}

	public String getString(String key) {
		return json.getString(key);
	}

	public byte getByteValue(String key) {
		return json.getByteValue(key);
	}

	public Byte getByte(String key) {
		return json.getByte(key);
	}

	public short getShortValue(String key) {
		return json.getShortValue(key);
	}

	public Short getShort(String key) {
		return json.getShort(key);
	}

	public int getIntValue(String key) {
		return json.getIntValue(key);
	}

	public Integer getInteger(String key) {
		return json.getInteger(key);
	}

	public long getLongValue(String key) {
		return json.getLongValue(key);
	}

	public Long getLong(String key) {
		return json.getLong(key);
	}

	public float getFloatValue(String key) {
		return json.getFloatValue(key);
	}

	public Float getFloat(String key) {
		return json.getFloat(key);
	}

	public double getDoubleValue(String key) {
		return json.getDoubleValue(key);
	}

	public Double getDouble(String key) {
		return json.getDouble(key);
	}

	public scw.json.JSONObject getJsonObject(String key) {
		com.alibaba.fastjson.JSONObject jo = json.getJSONObject(key);
		return jo == null ? null : new FastJSONObject(jo);
	}

	public scw.json.JSONArray getJsonArray(String key) {
		com.alibaba.fastjson.JSONArray jarr = json.getJSONArray(key);
		return jarr == null ? null : new FastJSONArray(jarr);
	}

	public <T> T getObject(String key, Class<T> type) {
		return json.getObject(key, type);
	}

	public Set<String> keySet() {
		return json.keySet();
	}

	public String toJSONString() {
		return json.toJSONString();
	}

	@Override
	public String toString() {
		return toJSONString();
	}

}

final class FastJSONArray implements scw.json.JSONArray {
	private final com.alibaba.fastjson.JSONArray jsonArray;

	public FastJSONArray(com.alibaba.fastjson.JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public String getString(int index) {
		return jsonArray.getString(index);
	}

	public byte getByteValue(int index) {
		return jsonArray.getByteValue(index);
	}

	public Byte getByte(int index) {
		return jsonArray.getByte(index);
	}

	public short getShortValue(int index) {
		return jsonArray.getShortValue(index);
	}

	public Short getShort(int index) {
		return jsonArray.getShort(index);
	}

	public int getIntValue(int index) {
		return jsonArray.getIntValue(index);
	}

	public Integer getInteger(int index) {
		return jsonArray.getInteger(index);
	}

	public long getLongValue(int index) {
		return jsonArray.getLongValue(index);
	}

	public Long getLong(int index) {
		return jsonArray.getLong(index);
	}

	public float getFloatValue(int index) {
		return jsonArray.getFloatValue(index);
	}

	public Float getFloat(int index) {
		return jsonArray.getFloat(index);
	}

	public double getDoubleValue(int index) {
		return jsonArray.getDoubleValue(index);
	}

	public Double getDouble(int index) {
		return jsonArray.getDouble(index);
	}

	public scw.json.JSONObject getJsonObject(int index) {
		com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(index);
		return jsonObject == null ? null : new FastJSONObject(jsonObject);
	}

	public scw.json.JSONArray getJsonArray(int index) {
		com.alibaba.fastjson.JSONArray jarr = jsonArray.getJSONArray(index);
		return jarr == null ? null : new FastJSONArray(jarr);
	}

	public <T> T getObject(int index, Class<T> type) {
		return jsonArray.getObject(index, type);
	}

	public int size() {
		return jsonArray.size();
	}

	public String toJSONString() {
		return jsonArray.toJSONString();
	}

	@Override
	public String toString() {
		return toJSONString();
	}
}

package scw.json.support.fastjson;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;

public final class FastJSONObjectWrapper implements scw.json.JSONObject {
	private static final long serialVersionUID = 1L;
	private com.alibaba.fastjson.JSONObject json;

	// 用于序列化
	protected FastJSONObjectWrapper() {
	};

	public FastJSONObjectWrapper(com.alibaba.fastjson.JSONObject json) {
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

	public scw.json.JSONObject getJSONObject(String key) {
		com.alibaba.fastjson.JSONObject jo = json.getJSONObject(key);
		return jo == null ? null : new FastJSONObjectWrapper(jo);
	}

	public scw.json.JSONArray getJSONArray(String key) {
		com.alibaba.fastjson.JSONArray jarr = json.getJSONArray(key);
		return jarr == null ? null : new FastJSONArrayWrapper(jarr);
	}

	public <T> T getObject(String key, Class<T> type) {
		return json.getObject(key, type);
	}

	public Set<String> keySet() {
		return json.keySet();
	}

	public String toJSONString() {
		return JSONObject.toJSONString(json, BaseProperyFilter.BASE_PROPERY_FILTER);
	}

	@Override
	public String toString() {
		return toJSONString();
	}

	public int size() {
		return json.size();
	}

	public boolean isEmpty() {
		return json.isEmpty();
	}

	public boolean getBooleanValue(String key) {
		return json.getBooleanValue(key);
	}

	public Boolean getBoolean(String key) {
		return json.getBoolean(key);
	}

	public <T> T getObject(String key, Type type) {
		return json.getObject(key, type);
	}

	public boolean containsKey(String key) {
		return json.containsKey(key);
	}

	public Object get(String key) {
		return json.get(key);
	}

	public Iterator<String> keys() {
		return json.keySet().iterator();
	}

	public scw.json.JSONObject put(String key, Object value) {
		json.put(key, value);
		return this;
	}

	public Object remove(String key) {
		return json.remove(key);
	}

}

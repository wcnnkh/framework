package scw.json.support.fastjson;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public final class FastJSONObject implements scw.json.JSONObject {
	private static final long serialVersionUID = 1L;
	private com.alibaba.fastjson.JSONObject json;

	// 用于序列化
	protected FastJSONObject() {
	};

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

	public scw.json.JSONObject getJSONObject(String key) {
		com.alibaba.fastjson.JSONObject jo = json.getJSONObject(key);
		return jo == null ? null : new FastJSONObject(jo);
	}

	public scw.json.JSONArray getJSONArray(String key) {
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

	public int size() {
		return json.size();
	}

	public boolean isEmpty() {
		return json.isEmpty();
	}

	public boolean containsKey(Object key) {
		return json.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return json.containsValue(value);
	}

	public Object get(Object key) {
		return json.get(key);
	}

	public Object put(String key, Object value) {
		return json.put(key, value);
	}

	public Object remove(Object key) {
		return json.remove(key);
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		json.putAll(m);
	}

	public void clear() {
		json.clear();
	}

	public Collection<Object> values() {
		return json.values();
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return json.entrySet();
	}

}

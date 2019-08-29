package scw.json.support.fastjson;

import java.lang.reflect.Type;
import java.util.Iterator;

import com.alibaba.fastjson.JSON;

import scw.json.JSONArray;

public final class FastJSONArrayWrapper implements scw.json.JSONArray {
	private static final long serialVersionUID = 1L;
	private com.alibaba.fastjson.JSONArray jsonArray;

	// 用于序列化
	protected FastJSONArrayWrapper() {
	};

	public FastJSONArrayWrapper(com.alibaba.fastjson.JSONArray jsonArray) {
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

	public scw.json.JSONObject getJSONObject(int index) {
		com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(index);
		return jsonObject == null ? null : new FastJSONObjectWrapper(jsonObject);
	}

	public scw.json.JSONArray getJSONArray(int index) {
		com.alibaba.fastjson.JSONArray jarr = jsonArray.getJSONArray(index);
		return jarr == null ? null : new FastJSONArrayWrapper(jarr);
	}

	public <T> T getObject(int index, Class<T> type) {
		return jsonArray.getObject(index, type);
	}

	public int size() {
		return jsonArray.size();
	}

	public String toJSONString() {
		return JSON.toJSONString(jsonArray, BaseProperyFilter.BASE_PROPERY_FILTER);
	}

	@Override
	public String toString() {
		return toJSONString();
	}

	public boolean isEmpty() {
		return jsonArray.isEmpty();
	}

	public Iterator<Object> iterator() {
		return jsonArray.iterator();
	}

	public JSONArray add(Object e) {
		jsonArray.add(e);
		return this;
	}

	public Object get(int index) {
		return jsonArray.get(index);
	}

	public JSONArray add(int index, Object element) {
		jsonArray.add(index, element);
		return this;
	}

	public Object remove(int index) {
		return jsonArray.remove(index);
	}

	public boolean getBooleanValue(int index) {
		return jsonArray.getBooleanValue(index);
	}

	public Boolean getBoolean(int index) {
		return jsonArray.getBoolean(index);
	}

	public <T> T getObject(int index, Type type) {
		return jsonArray.getObject(index, type);
	}
}

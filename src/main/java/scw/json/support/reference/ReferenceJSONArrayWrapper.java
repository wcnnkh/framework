package scw.json.support.reference;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;

import scw.core.utils.StringUtils;
import scw.json.JSONArray;
import scw.json.JSONObject;

public class ReferenceJSONArrayWrapper implements JSONArray {
	private static final long serialVersionUID = 1L;
	private final scw.json.reference.JSONArray jsonArray;

	public ReferenceJSONArrayWrapper(scw.json.reference.JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public String getString(int index) {
		return jsonArray == null ? null : jsonArray.optString(index);
	}

	public byte getByteValue(int index) {
		return StringUtils.parseByte(getString(index));
	}

	public Byte getByte(int index) {
		return StringUtils.parseByte(getString(index), null);
	}

	public boolean getBooleanValue(int index) {
		return StringUtils.parseBoolean(getString(index));
	}

	public Boolean getBoolean(int index) {
		return StringUtils.parseBoolean(getString(index), null);
	}

	public short getShortValue(int index) {
		return StringUtils.parseShort(getString(index));
	}

	public Short getShort(int index) {
		return StringUtils.parseShort(getString(index), null);
	}

	public int getIntValue(int index) {
		return StringUtils.parseInt(getString(index));
	}

	public Integer getInteger(int index) {
		return StringUtils.parseInt(getString(index), null);
	}

	public long getLongValue(int index) {
		return StringUtils.parseLong(getString(index));
	}

	public Long getLong(int index) {
		return StringUtils.parseLong(getString(index), null);
	}

	public float getFloatValue(int index) {
		return StringUtils.parseFloat(getString(index));
	}

	public Float getFloat(int index) {
		return StringUtils.parseFloat(getString(index), null);
	}

	public double getDoubleValue(int index) {
		return StringUtils.parseDouble(getString(index));
	}

	public Double getDouble(int index) {
		return StringUtils.parseDouble(getString(index), null);
	}

	public JSONObject getJSONObject(int index) {
		scw.json.reference.JSONObject jsonObject = jsonArray.optJSONObject(index);
		if (jsonObject == null) {
			String text = getString(index);
			if (text != null) {
				jsonObject = new scw.json.reference.JSONObject(text);
			}
		}

		if (jsonObject == null) {
			return null;
		}

		return new ReferenceJSONObjectWrapper(jsonObject);
	}

	public JSONArray getJSONArray(int index) {
		scw.json.reference.JSONArray json = jsonArray.getJSONArray(index);
		if (json == null) {
			String text = getString(index);
			if (text != null) {
				json = new scw.json.reference.JSONArray(text);
			}
		}

		if (json == null) {
			return null;
		}

		return new ReferenceJSONArrayWrapper(json);
	}

	public <T> T getObject(int index, Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T getObject(int index, Type type) {
		// TODO Auto-generated method stub
		return null;
	}

	public String toJSONString() {
		return jsonArray == null ? null : jsonArray.toString();
	}

	public int size() {
		return jsonArray == null ? 0 : jsonArray.length();
	}

	public boolean isEmpty() {
		return jsonArray == null || jsonArray.isEmpty();
	}

	public Iterator<Object> iterator() {
		return jsonArray == null ? Collections.emptyIterator() : jsonArray.iterator();
	}

	public JSONArray add(Object e) {
		jsonArray.put(e);
		return this;
	}

	public Object get(int index) {
		return jsonArray == null ? null : jsonArray.get(index);
	}

	public JSONArray add(int index, Object element) {
		jsonArray.put(index, element);
		return this;
	}

	public Object remove(int index) {
		if (jsonArray == null) {
			return null;
		}

		return jsonArray.remove(index);
	}
}

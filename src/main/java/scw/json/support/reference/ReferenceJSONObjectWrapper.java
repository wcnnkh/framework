package scw.json.support.reference;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Set;

import scw.core.utils.StringUtils;
import scw.json.JSONArray;
import scw.json.JSONObject;

public class ReferenceJSONObjectWrapper implements JSONObject {
	private static final long serialVersionUID = 1L;
	private final scw.json.reference.JSONObject jsonObject;

	public ReferenceJSONObjectWrapper(scw.json.reference.JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public String getString(String key) {
		return jsonObject == null ? null : jsonObject.optString(key);
	}

	public byte getByteValue(String key) {
		return StringUtils.parseByte(getString(key));
	}

	public Byte getByte(String key) {
		return StringUtils.parseByte(getString(key), null);
	}

	public boolean getBooleanValue(String key) {
		return StringUtils.parseBoolean(getString(key));
	}

	public Boolean getBoolean(String key) {
		return StringUtils.parseBoolean(getString(key), null);
	}

	public short getShortValue(String key) {
		return StringUtils.parseShort(getString(key));
	}

	public Short getShort(String key) {
		return StringUtils.parseShort(getString(key), null);
	}

	public int getIntValue(String key) {
		return StringUtils.parseInt(getString(key));
	}

	public Integer getInteger(String key) {
		return StringUtils.parseInt(getString(key), null);
	}

	public long getLongValue(String key) {
		return StringUtils.parseLong(getString(key));
	}

	public Long getLong(String key) {
		return StringUtils.parseLong(getString(key), null);
	}

	public float getFloatValue(String key) {
		return StringUtils.parseFloat(getString(key));
	}

	public Float getFloat(String key) {
		return StringUtils.parseFloat(getString(key), null);
	}

	public double getDoubleValue(String key) {
		return StringUtils.parseDouble(getString(key));
	}

	public Double getDouble(String key) {
		return StringUtils.parseDouble(getString(key), null);
	}

	public JSONObject getJSONObject(String key) {
		scw.json.reference.JSONObject json = jsonObject.optJSONObject(key);
		if (json == null) {
			String text = getString(key);
			if (text != null) {
				json = new scw.json.reference.JSONObject(text);
			}
		}

		if (json == null) {
			return null;
		}
		return new ReferenceJSONObjectWrapper(json);
	}

	public JSONArray getJSONArray(String key) {
		scw.json.reference.JSONArray jsonArray = jsonObject.optJSONArray(key);
		if (jsonArray == null) {
			String text = getString(key);
			if (text != null) {
				jsonArray = new scw.json.reference.JSONArray(text);
			}
		}

		if (jsonArray == null) {
			return null;
		}

		return new ReferenceJSONArrayWrapper(jsonArray);
	}

	public <T> T getObject(String key, Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	public String toJSONString() {
		return jsonObject == null ? null : jsonObject.toString();
	}

	public <T> T getObject(String key, Type type) {
		// TODO Auto-generated method stub
		return null;
	}

	public int size() {
		return jsonObject.length();
	}

	public boolean isEmpty() {
		return jsonObject.isEmpty();
	}

	public boolean containsKey(String key) {
		return jsonObject.has(key);
	}

	public Object get(String key) {
		return jsonObject.opt(key);
	}

	public JSONObject put(String key, Object value) {
		jsonObject.put(key, value);
		return this;
	}

	public Object remove(String key) {
		return jsonObject.remove(key);
	}

	public Set<String> keySet() {
		return jsonObject.keySet();
	}

	public Iterator<String> keys() {
		return jsonObject.keys();
	}
}

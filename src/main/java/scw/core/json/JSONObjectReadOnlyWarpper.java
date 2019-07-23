package scw.core.json;

public class JSONObjectReadOnlyWarpper implements JSONObjectReadOnly {
	private static final long serialVersionUID = 1L;
	private JSONObjectReadOnly jsonObjectReadOnly;

	public JSONObjectReadOnlyWarpper(JSONObjectReadOnly jsonObjectReadOnly) {
		this.jsonObjectReadOnly = jsonObjectReadOnly;
	}

	public String getString(String key) {
		return jsonObjectReadOnly.getString(key);
	}

	public byte getByteValue(String key) {
		return jsonObjectReadOnly.getByteValue(key);
	}

	public Byte getByte(String key) {
		return jsonObjectReadOnly.getByte(key);
	}

	public boolean getBooleanValue(String key) {
		return jsonObjectReadOnly.getBooleanValue(key);
	}

	public Boolean getBoolean(String key) {
		return jsonObjectReadOnly.getBoolean(key);
	}

	public short getShortValue(String key) {
		return jsonObjectReadOnly.getShortValue(key);
	}

	public Short getShort(String key) {
		return jsonObjectReadOnly.getShort(key);
	}

	public int getIntValue(String key) {
		return jsonObjectReadOnly.getIntValue(key);
	}

	public Integer getInteger(String key) {
		return jsonObjectReadOnly.getInteger(key);
	}

	public long getLongValue(String key) {
		return jsonObjectReadOnly.getLongValue(key);
	}

	public Long getLong(String key) {
		return jsonObjectReadOnly.getLong(key);
	}

	public float getFloatValue(String key) {
		return jsonObjectReadOnly.getFloatValue(key);
	}

	public Float getFloat(String key) {
		return jsonObjectReadOnly.getFloat(key);
	}

	public double getDoubleValue(String key) {
		return jsonObjectReadOnly.getDoubleValue(key);
	}

	public Double getDouble(String key) {
		return jsonObjectReadOnly.getDouble(key);
	}

	public JSONObject getJSONObject(String key) {
		return jsonObjectReadOnly.getJSONObject(key);
	}

	public JSONArray getJSONArray(String key) {
		return jsonObjectReadOnly.getJSONArray(key);
	}

	public <T> T getObject(String key, Class<T> type) {
		return jsonObjectReadOnly.getObject(key, type);
	}

	public String toJSONString() {
		return jsonObjectReadOnly.toJSONString();
	}
}

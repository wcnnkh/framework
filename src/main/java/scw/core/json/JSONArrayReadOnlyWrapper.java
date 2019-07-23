package scw.core.json;

public class JSONArrayReadOnlyWrapper implements JSONArrayReadOnly {
	private static final long serialVersionUID = 1L;
	private JSONArrayReadOnly jsonArrayReadOnly;

	public JSONArrayReadOnlyWrapper(JSONArrayReadOnly jsonArrayReadOnly) {
		this.jsonArrayReadOnly = jsonArrayReadOnly;
	}

	public String getString(int index) {
		return jsonArrayReadOnly.getString(index);
	}

	public byte getByteValue(int index) {
		return jsonArrayReadOnly.getByteValue(index);
	}

	public Byte getByte(int index) {
		return jsonArrayReadOnly.getByte(index);
	}

	public boolean getBooleanValue(int index) {
		return jsonArrayReadOnly.getBooleanValue(index);
	}

	public Boolean getBoolean(int index) {
		return jsonArrayReadOnly.getBoolean(index);
	}

	public short getShortValue(int index) {
		return jsonArrayReadOnly.getShortValue(index);
	}

	public Short getShort(int index) {
		return jsonArrayReadOnly.getShort(index);
	}

	public int getIntValue(int index) {
		return jsonArrayReadOnly.getIntValue(index);
	}

	public Integer getInteger(int index) {
		return jsonArrayReadOnly.getInteger(index);
	}

	public long getLongValue(int index) {
		return jsonArrayReadOnly.getLongValue(index);
	}

	public Long getLong(int index) {
		return jsonArrayReadOnly.getLong(index);
	}

	public float getFloatValue(int index) {
		return jsonArrayReadOnly.getFloatValue(index);
	}

	public Float getFloat(int index) {
		return jsonArrayReadOnly.getFloat(index);
	}

	public double getDoubleValue(int index) {
		return jsonArrayReadOnly.getDoubleValue(index);
	}

	public Double getDouble(int index) {
		return jsonArrayReadOnly.getDouble(index);
	}

	public JSONObject getJSONObject(int index) {
		return jsonArrayReadOnly.getJSONObject(index);
	}

	public JSONArray getJSONArray(int index) {
		return jsonArrayReadOnly.getJSONArray(index);
	}

	public <T> T getObject(int index, Class<T> type) {
		return jsonArrayReadOnly.getObject(index, type);
	}

	public String toJSONString() {
		return jsonArrayReadOnly.toJSONString();
	}

}

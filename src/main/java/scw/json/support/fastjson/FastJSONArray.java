package scw.json.support.fastjson;

public final class FastJSONArray implements scw.json.JSONArray {
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

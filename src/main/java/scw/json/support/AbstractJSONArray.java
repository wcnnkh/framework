package scw.json.support;

import scw.core.utils.StringUtils;
import scw.json.JSONArray;

public abstract class AbstractJSONArray implements JSONArray {
	private static final long serialVersionUID = 1L;

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
}

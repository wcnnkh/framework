package scw.json.support;

import scw.core.utils.StringUtils;
import scw.json.JSONObject;

public abstract class AbstractJSONObject implements JSONObject {
	private static final long serialVersionUID = 1L;

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
}

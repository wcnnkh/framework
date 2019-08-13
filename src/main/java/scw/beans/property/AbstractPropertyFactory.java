package scw.beans.property;

import java.math.BigDecimal;
import java.math.BigInteger;

import scw.core.ValueFactory;
import scw.core.utils.StringParse;

public abstract class AbstractPropertyFactory implements
		scw.core.PropertyFactory, ValueFactory<String> {
	public String getString(String key) {
		return getProperty(key);
	}

	public BigInteger getBigInteger(String key) {
		return StringParse.DEFAULT.getBigInteger(getString(key));
	}

	public BigDecimal getBigDecimal(String key) {
		return StringParse.DEFAULT.getBigDecimal(getString(key));
	}

	public Class<?> getClass(String key) {
		return StringParse.DEFAULT.getClass(getString(key));
	}

	@SuppressWarnings("rawtypes")
	public Enum<?> getEnum(String key, Class<? extends Enum> enumType) {
		return StringParse.DEFAULT.getEnum(getString(key), enumType);
	}

	public <E> E[] getArray(String key, Class<E> type) {
		return StringParse.DEFAULT.getArray(getString(key), type);
	}

	public Object getObject(String key, Class<?> type) {
		return StringParse.DEFAULT.getObject(getString(key), type);
	}

	public Byte getByte(String key) {
		return StringParse.DEFAULT.getByte(getString(key));
	}

	public byte getByteValue(String key) {
		return StringParse.DEFAULT.getByteValue(getString(key));
	}

	public Short getShort(String key) {
		return StringParse.DEFAULT.getShort(getString(key));
	}

	public short getShortValue(String key) {
		return StringParse.DEFAULT.getShortValue(getString(key));
	}

	public Integer getInteger(String key) {
		return StringParse.DEFAULT.getInteger(getString(key));
	}

	public int getIntValue(String key) {
		return StringParse.DEFAULT.getIntValue(getString(key));
	}

	public Long getLong(String key) {
		return StringParse.DEFAULT.getLong(getString(key));
	}

	public long getLongValue(String key) {
		return StringParse.DEFAULT.getLongValue(getString(key));
	}

	public Boolean getBoolean(String key) {
		return StringParse.DEFAULT.getBoolean(getString(key));
	}

	public boolean getBooleanValue(String key) {
		return StringParse.DEFAULT.getBooleanValue(getString(key));
	}

	public Float getFloat(String key) {
		return StringParse.DEFAULT.getFloat(getString(key));
	}

	public float getFloatValue(String key) {
		return StringParse.DEFAULT.getFloatValue(getString(key));
	}

	public Double getDouble(String key) {
		return StringParse.DEFAULT.getDouble(getString(key));
	}

	public double getDoubleValue(String key) {
		return StringParse.DEFAULT.getDoubleValue(getString(key));
	}

	public char getChar(String key) {
		return StringParse.DEFAULT.getChar(getString(key));
	}

	public Character getCharacter(String key) {
		return StringParse.DEFAULT.getCharacter(getString(key));
	}

}

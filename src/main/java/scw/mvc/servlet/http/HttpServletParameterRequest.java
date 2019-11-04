package scw.mvc.servlet.http;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;

import scw.mvc.parameter.ParameterRequest;

public class HttpServletParameterRequest extends MyHttpServletRequest implements ParameterRequest {
	private final HttpServletChannel httpServletChannel;

	public HttpServletParameterRequest(HttpServletChannel httpServletChannel, HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
		this.httpServletChannel = httpServletChannel;
	}

	public final HttpServletChannel getHttpServletChannel() {
		return httpServletChannel;
	}

	public Byte getByte(String name) {
		return httpServletChannel.getByte(name);
	}

	public byte getByteValue(String name) {
		return httpServletChannel.getByteValue(name);
	}

	public Short getShort(String name) {
		return httpServletChannel.getShort(name);
	}

	public short getShortValue(String name) {
		return httpServletChannel.getShortValue(name);
	}

	public Integer getInteger(String name) {
		return httpServletChannel.getInteger(name);
	}

	public int getIntValue(String name) {
		return httpServletChannel.getIntValue(name);
	}

	public Long getLong(String name) {
		return httpServletChannel.getLong(name);
	}

	public long getLongValue(String name) {
		return httpServletChannel.getLongValue(name);
	}

	public Boolean getBoolean(String name) {
		return httpServletChannel.getBoolean(name);
	}

	public boolean getBooleanValue(String name) {
		return httpServletChannel.getBooleanValue(name);
	}

	public Float getFloat(String name) {
		return httpServletChannel.getFloat(name);
	}

	public float getFloatValue(String name) {
		return httpServletChannel.getFloatValue(name);
	}

	public Double getDouble(String name) {
		return httpServletChannel.getDouble(name);
	}

	public double getDoubleValue(String name) {
		return httpServletChannel.getDoubleValue(name);
	}

	public char getChar(String name) {
		return httpServletChannel.getChar(name);
	}

	public Character getCharacter(String name) {
		return httpServletChannel.getCharacter(name);
	}

	public String getString(String name) {
		return httpServletChannel.getString(name);
	}

	public BigInteger getBigInteger(String name) {
		return httpServletChannel.getBigInteger(name);
	}

	public BigDecimal getBigDecimal(String name) {
		return httpServletChannel.getBigDecimal(name);
	}

	public Class<?> getClass(String name) {
		return httpServletChannel.getClass(name);
	}

	@SuppressWarnings("rawtypes")
	public Enum getEnum(String name, Class<? extends Enum> type) {
		return httpServletChannel.getEnum(name, type);
	}

	public <E> E[] getArray(String name, Class<E> type) {
		return httpServletChannel.getArray(name, type);
	}

	public Object getObject(String name, Class<?> type) {
		return httpServletChannel.getObject(name, type);
	}

	public Object getObject(String name, Type type) {
		return httpServletChannel.getObject(name, type);
	}

}

package scw.mvc.http;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import scw.mvc.parameter.ParameterRequest;

public class HttpParameterRequest extends HttpRequestWrapper implements ParameterRequest {
	private final HttpChannel httpChannel;

	public HttpParameterRequest(HttpRequest httpRequest, HttpChannel httpChannel) {
		super(httpRequest);
		this.httpChannel = httpChannel;
	}

	public final HttpChannel getHttpChannel() {
		return httpChannel;
	}

	public String getString(String name) {
		return httpChannel.getString(name);
	}

	public BigInteger getBigInteger(String name) {
		return httpChannel.getBigInteger(name);
	}

	public BigDecimal getBigDecimal(String name) {
		return httpChannel.getBigDecimal(name);
	}

	public Class<?> getClass(String name) {
		return httpChannel.getClass(name);
	}

	@SuppressWarnings("rawtypes")
	public Enum getEnum(String name, Class<? extends Enum> type) {
		return httpChannel.getEnum(name, type);
	}

	public <E> E[] getArray(String text, Class<E> type) {
		return httpChannel.getArray(text, type);
	}

	public Object getObject(String name, Class<?> type) {
		return httpChannel.getObject(name, type);
	}

	public Object getObject(String name, Type type) {
		return httpChannel.getObject(name, type);
	}

	public Byte getByte(String name) {
		return httpChannel.getByte(name);
	}

	public byte getByteValue(String name) {
		return httpChannel.getByteValue(name);
	}

	public Short getShort(String name) {
		return httpChannel.getShort(name);
	}

	public short getShortValue(String name) {
		return httpChannel.getShortValue(name);
	}

	public Integer getInteger(String name) {
		return httpChannel.getInteger(name);
	}

	public int getIntValue(String name) {
		return httpChannel.getIntValue(name);
	}

	public Long getLong(String name) {
		return httpChannel.getLong(name);
	}

	public long getLongValue(String name) {
		return httpChannel.getLongValue(name);
	}

	public Boolean getBoolean(String name) {
		return httpChannel.getBoolean(name);
	}

	public boolean getBooleanValue(String name) {
		return httpChannel.getBooleanValue(name);
	}

	public Float getFloat(String name) {
		return httpChannel.getFloat(name);
	}

	public float getFloatValue(String name) {
		return httpChannel.getFloatValue(name);
	}

	public Double getDouble(String name) {
		return httpChannel.getDouble(name);
	}

	public double getDoubleValue(String name) {
		return httpChannel.getDoubleValue(name);
	}

	public char getChar(String name) {
		return httpChannel.getChar(name);
	}

	public Character getCharacter(String name) {
		return httpChannel.getCharacter(name);
	}

	public <T> T getObject(Class<T> type) {
		return httpChannel.getObject(type);
	}

	public <T> T getObject(Type type) {
		return httpChannel.getObject(type);
	}
}

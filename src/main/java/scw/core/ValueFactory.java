package scw.core;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import scw.core.annotation.Ignore;

@Ignore
public interface ValueFactory<T> extends PrimitiveTypeValueFactory<T> {
	String getString(T data);

	BigInteger getBigInteger(T data);

	BigDecimal getBigDecimal(T data);

	Class<?> getClass(T data);

	@SuppressWarnings("rawtypes")
	Enum getEnum(T data, Class<? extends Enum> type);

	<E> E[] getArray(T text, Class<E> type);

	Object getObject(T data, Class<?> type);

	Object getObject(T data, Type type);
}

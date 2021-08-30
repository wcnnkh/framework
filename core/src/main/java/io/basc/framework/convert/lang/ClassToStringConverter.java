package io.basc.framework.convert.lang;

import io.basc.framework.convert.Converter;
import io.basc.framework.lang.NotSupportedException;

import java.io.Serializable;

public class ClassToStringConverter implements Converter<Class<?>, String>, Serializable {
	private static final long serialVersionUID = 1L;
	public static final Converter<Class<?>, String> NAME = new ClassToStringConverter(1);
	public static final Converter<Class<?>, String> SIMPLE_NAME = new ClassToStringConverter(2);
	public static final Converter<Class<?>, String> CANONICAL_NAME = new ClassToStringConverter(3);
	public static final Converter<Class<?>, String> TYPE_NAME = new ClassToStringConverter(4);
	public static final Converter<Class<?>, String> GENERIC_STRING = new ClassToStringConverter(5);
	public static final Converter<Class<?>, String> STRING = new ClassToStringConverter(6);

	private int type;

	private ClassToStringConverter(int type) {
		this.type = type;
	}

	@Override
	public String convert(Class<?> o) {
		switch (type) {
		case 1:
			return o.getName();
		case 2:
			return o.getSimpleName();
		case 3:
			return o.getCanonicalName();
		case 4:
			return o.getTypeName();
		case 5:
			return o.toGenericString();
		case 6:
			return o.toString();
		default:
			throw new NotSupportedException("type=" + type + ", class=" + o);
		}
	}

}

package io.basc.framework.util;

import java.io.Serializable;
import java.util.function.Function;

import io.basc.framework.lang.NotSupportedException;

public class ClassToString implements Function<Class<?>, String>, Serializable {
	private static final long serialVersionUID = 1L;
	public static final Function<Class<?>, String> NAME = new ClassToString(1);
	public static final Function<Class<?>, String> SIMPLE_NAME = new ClassToString(2);
	public static final Function<Class<?>, String> CANONICAL_NAME = new ClassToString(3);
	public static final Function<Class<?>, String> TYPE_NAME = new ClassToString(4);
	public static final Function<Class<?>, String> GENERIC_STRING = new ClassToString(5);
	public static final Function<Class<?>, String> STRING = new ClassToString(6);

	private int type;

	private ClassToString(int type) {
		this.type = type;
	}

	@Override
	public String apply(Class<?> o) {
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

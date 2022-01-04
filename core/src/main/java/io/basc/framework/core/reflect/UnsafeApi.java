package io.basc.framework.core.reflect;

import java.lang.reflect.Field;

import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.stream.Processor;

public class UnsafeApi extends Api {
	private static final Processor<Class<?>, Object, Throwable> PROCESSOR = (C) -> {
		Field f = C.getDeclaredField("theUnsafe");
		ReflectionUtils.makeAccessible(f);
		return f.get(null);
	};

	public UnsafeApi() {
		super(ClassUtils.getClass("un.misc.Unsafe", null), PROCESSOR);
	}
}

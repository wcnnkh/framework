package io.basc.framework.core.reflect;

import java.lang.reflect.Method;

import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.stream.Processor;

public class ReflectionFactoryApi extends Api {

	private static final Processor<Class<?>, Object, Throwable> PROCESSOR = (c) -> {
		Method method = c.getMethod("getReflectionFactory");
		return method.invoke(null);
	};

	public ReflectionFactoryApi() {
		super(ClassUtils.getClass("sun.reflect.ReflectionFactory", null), PROCESSOR.caching());
	}
}

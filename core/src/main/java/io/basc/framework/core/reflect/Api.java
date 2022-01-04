package io.basc.framework.core.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.stream.Processor;

public class Api implements Supplier<Object> {
	private final Class<?> declaringClass;
	private final Processor<Class<?>, Object, Throwable> processor;

	public Api(@Nullable Class<?> declaringClass, @Nullable Processor<Class<?>, Object, Throwable> processor) {
		this.declaringClass = declaringClass;
		this.processor = processor;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	/**
	 * 是否可用
	 * @return
	 */
	public boolean isAvailable() {
		return declaringClass != null;
	}

	public Object get() {
		if (declaringClass == null) {
			throw new IllegalStateException("Unavailable API");
		}

		if (this.processor == null) {
			return null;
		}

		try {
			return processor.process(this.declaringClass);
		} catch (Throwable e) {
			throw new IllegalStateException(this.declaringClass.getName(), e);
		}
	}

	@Nullable
	public Method getMethod(String name, Class<?>... parameterTypes) {
		if (this.declaringClass == null) {
			return null;
		}
		return ReflectionUtils.findMethod(declaringClass, name, parameterTypes);
	}

	public Object invoke(Method method, Object... args) {
		Assert.requiredArgument(method != null, "method");
		ReflectionUtils.makeAccessible(method);
		return ReflectionUtils.invokeMethod(method, Modifier.isStatic(method.getModifiers()) ? null : get(), args);
	}

	/**
	 * 调用指定方法名
	 * 
	 * @param methodName
	 * @param params     parameterTypes和params各一半，如 {String.class, "a"}
	 * @return
	 */
	public Object invoke(String methodName, Object... params) {
		Assert.requiredArgument(isAvailable(), "declaringClass");
		Assert.requiredArgument(StringUtils.hasText(methodName), "methodName");
		Assert.requiredArgument(params != null && params.length % 2 == 0, "params");
		Class<?>[] parameterTypes = new Class<?>[params.length / 2];
		System.arraycopy(params, 0, parameterTypes, 0, parameterTypes.length);
		Method method = getMethod(methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException(declaringClass + " not found method[" + methodName + "] parameterTypes"
					+ Arrays.toString(parameterTypes));
		}
		Object[] args = new Object[parameterTypes.length];
		System.arraycopy(params, parameterTypes.length, args, 0, args.length);
		return invoke(method, args);
	}
}

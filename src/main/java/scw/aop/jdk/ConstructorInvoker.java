package scw.aop.jdk;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import scw.aop.Invoker;
import scw.common.utils.ClassUtils;

public class ConstructorInvoker implements Invoker {
	private Constructor<?> constructor;

	public ConstructorInvoker(Class<?> type, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		this.constructor = getConstructor(type, parameterTypes);
	}

	public ConstructorInvoker(String className, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		this(Class.forName(className), parameterTypes);
	}

	public ConstructorInvoker(Class<?> type, String... parameterTypes)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		this.constructor = getConstructor(type, ClassUtils.forName(parameterTypes));
	}

	public ConstructorInvoker(String className, String... parameterTypes)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		this.constructor = getConstructor(Class.forName(className), ClassUtils.forName(className));
	}

	public static Constructor<?> getConstructor(Class<?> type, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		Constructor<?> constructor = type.getConstructor(parameterTypes);
		if (!Modifier.isPublic(constructor.getModifiers())) {
			constructor.setAccessible(true);
		}
		return constructor;
	}

	public Object invoke(Object... args) throws Throwable {
		return constructor.newInstance(args);
	}

}

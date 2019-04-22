package scw.aop.jdk;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import scw.aop.Invoker;
import scw.common.exception.NotFoundException;
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

	public ConstructorInvoker(Class<?> type, Object... params) {
		for (Constructor<?> constructor : type.getDeclaredConstructors()) {
			Class<?>[] types = constructor.getParameterTypes();
			if (types.length == params.length) {
				boolean find = true;
				for (int i = 0; i < types.length; i++) {
					Object v = params[i];
					if (v == null) {
						continue;
					}

					if (!ClassUtils.isAssignableValue(types[i], v)) {
						find = false;
					}
				}

				if (find) {
					this.constructor = constructor;
					break;
				}
			}
		}

		if (this.constructor == null) {
			throw new NotFoundException(type.getName() + "找不到指定构造方法");
		}
		
		if (!Modifier.isPublic(constructor.getModifiers())) {
			constructor.setAccessible(true);
		}
	}

	public static Constructor<?> getConstructor(Class<?> type, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		Constructor<?> constructor = type.getConstructor(parameterTypes);
		if (!Modifier.isPublic(constructor.getModifiers())) {
			constructor.setAccessible(true);
		}
		return constructor;
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	public void setConstructor(Constructor<?> constructor) {
		this.constructor = constructor;
	}

	public Object invoke(Object... args) throws Throwable {
		return constructor.newInstance(args);
	}

}

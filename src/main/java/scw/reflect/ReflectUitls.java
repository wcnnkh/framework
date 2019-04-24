package scw.reflect;

import scw.common.utils.StringUtils;

public final class ReflectUitls {
	private ReflectUitls() {
	};

	public static Invoker transformInvoker(Object bean, Method method) {
		return new transformInvoker(method, bean);
	}

	public static java.lang.reflect.Method findSetterMethod(Class<?> clazz, String fieldName, boolean isPublic) {
		String methodName = "set" + StringUtils.toUpperCase(fieldName, 0, 1);
		for (java.lang.reflect.Method method : isPublic ? clazz.getMethods() : clazz.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				continue;
			}

			if (method.getParameterTypes().length != 1) {
				continue;
			}

			return method;
		}

		return null;
	}
}

final class transformInvoker implements Invoker {
	private final Method method;
	private final Object bean;

	public transformInvoker(Method method, Object bean) {
		this.method = method;
		this.bean = bean;
	}

	public Object invoke(Object... args) throws Throwable {
		return method.invoke(bean, args);
	}

}

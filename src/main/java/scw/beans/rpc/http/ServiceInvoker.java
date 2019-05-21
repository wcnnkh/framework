package scw.beans.rpc.http;

import java.lang.reflect.Method;

import scw.core.utils.NumberUtils;

public class ServiceInvoker {
	private Method method;

	public ServiceInvoker(Method method) {
		this.method = method;
	}

	@SuppressWarnings("unchecked")
	public Object invoke(Object bean, Object... args) throws Throwable {
		Object[] values = new Object[args.length];
		Class<?>[] types = method.getParameterTypes();
		for (int i = 0; i < values.length; i++) {
			Object v = args[i];
			if (v == null) {
				values[i] = v;
				continue;
			}

			if (v instanceof Number) {
				if (types[i].isPrimitive()) {
					values[i] = NumberUtils.converPrimitive((Number) v, types[i]);
				} else if (Number.class.isAssignableFrom(types[i])) {
					values[i] = NumberUtils.convertNumberToTargetClass((Number) v, (Class<Number>) types[i]);
				} else {
					values[i] = v;
				}
			} else {
				values[i] = v;
			}
		}
		return method.invoke(bean, values);
	}

}

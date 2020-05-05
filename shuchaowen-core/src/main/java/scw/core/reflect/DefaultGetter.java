package scw.core.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class DefaultGetter extends AbstractGetterSetter implements Getter {
	private static final long serialVersionUID = 1L;

	public DefaultGetter(Class<?> declaringClass, String name, Field field,
			Method method) {
		super(declaringClass, name, field, method);
	}

	public Class<?> getType() {
		Method method = getMethod();
		if (method != null) {
			return method.getReturnType();
		}

		Field field = getField();
		if (field != null) {
			return field.getType();
		}
		throw createNotSupportException();
	}

	public Type getGenericType() {
		Method method = getMethod();
		if (method != null) {
			return method.getGenericReturnType();
		}

		Field field = getField();
		if (field != null) {
			return field.getGenericType();
		}
		throw createNotSupportException();
	}
}

package scw.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class DefaultGetter extends AbstractFieldDescriptor implements Getter {
	private static final long serialVersionUID = 1L;
	private final String name;

	public DefaultGetter(Class<?> declaringClass, String name, Field field,
			Method method) {
		super(declaringClass, field, method);
		this.name = name;
	}
	
	public String getName() {
		return name;
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

package scw.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class DefaultSetter extends AbstractFieldDescriptor implements Setter{
	private static final long serialVersionUID = 1L;
	private final String name;

	public DefaultSetter(Class<?> declaringClass, String name, Field field,
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
			return method.getParameterTypes()[0];
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
			return method.getGenericParameterTypes()[0];
		}

		Field field = getField();
		if (field != null) {
			return field.getGenericType();
		}
		throw createNotSupportException();
	}
}

package scw.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class DefaultSetter extends AbstractGetterSetter implements Setter{
	private static final long serialVersionUID = 1L;

	public DefaultSetter(Class<?> declaringClass, String name, Field field,
			Method method) {
		super(declaringClass, name, field, method);
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
			return method.getGenericExceptionTypes()[0];
		}

		Field field = getField();
		if (field != null) {
			return field.getGenericType();
		}
		throw createNotSupportException();
	}
}

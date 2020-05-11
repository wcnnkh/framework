package scw.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.core.reflect.ReflectionUtils;

abstract class AbstractGetterSetter extends AbstractFieldDescriptor{
	private static final long serialVersionUID = 1L;
	private final String name;

	public AbstractGetterSetter(Class<?> declaringClass, String name,
			Field field, Method method) {
		super(declaringClass, field, method);
		this.name = name;
	}

	public Object get(Object instance) {
		Method method = getMethod();
		if (method != null) {
			ReflectionUtils.makeAccessible(method);
			return ReflectionUtils.invokeMethod(method, Modifier.isStatic(method.getModifiers()) ? null
					: instance);
		}

		java.lang.reflect.Field field = getField();
		if (field != null) {
			ReflectionUtils.makeAccessible(field);
			return ReflectionUtils.getField(field, Modifier.isStatic(field.getModifiers()) ? null
					: instance);
		}
		throw createNotSupportException();
	}

	public void set(Object instance, Object value) {
		Method method = getMethod();
		if (method != null) {
			ReflectionUtils.makeAccessible(method);
			ReflectionUtils.invokeMethod(method, Modifier.isStatic(method.getModifiers()) ? null
					: instance, value);
			return ;
		}

		java.lang.reflect.Field field = getField();
		if (field != null) {
			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, Modifier.isStatic(field.getModifiers()) ? null : instance, value);
			return ;
		}
		throw createNotSupportException();
	}

	public String getName() {
		return name;
	}
}

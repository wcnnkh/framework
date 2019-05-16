package scw.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.core.reflect.ReflectUtils;

public final class FieldInfo {
	private Field field;
	private Method getter;
	private Method setter;

	public FieldInfo(Class<?> clz, Field field) {
		this.field = field;
		this.getter = ReflectUtils.getGetterMethod(clz, field, false);
		this.setter = ReflectUtils.getSetterMethod(clz, field, false);
	}

	public String getName() {
		return field.getName();
	}

	public Field getField() {
		return field;
	}

	public Class<?> getType() {
		return field.getType();
	}

	public Method getGetter() {
		return getter;
	}

	public Method getSetter() {
		return setter;
	}

	public Object get(Object obj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (getter == null) {
			return forceGet(obj);
		} else {
			return getter.invoke(obj);
		}
	}

	public void set(Object obj, Object value)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (setter == null) {
			forceSet(obj, value);
		} else {
			setter.invoke(obj, value);
		}
	}

	/**
	 * 不调用get方法直接获取值
	 * 
	 * @param obj
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Object forceGet(Object obj) throws IllegalArgumentException, IllegalAccessException {
		return field.get(obj);
	}

	/**
	 * 不调用set方法直接设置值
	 * 
	 * @param obj
	 * @param value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void forceSet(Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
		field.set(obj, value);
	}

	public boolean isStatic() {
		return Modifier.isStatic(field.getModifiers());
	}

	public boolean isFinal() {
		return Modifier.isFinal(field.getModifiers());
	}

	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return field.getAnnotation(type);
	}
}

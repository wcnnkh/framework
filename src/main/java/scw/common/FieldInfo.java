package scw.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;

public final class FieldInfo {
	private String name;
	private Field field;
	private Class<?> type;
	private Method getter;
	private Method setter;

	public FieldInfo(Class<?> clz, Field field) {
		this.field = field;
		this.name = field.getName();
		this.type = field.getType();
		this.field.setAccessible(true);

		try {
			this.getter = clz.getDeclaredMethod("is" + StringUtils.toUpperCase(name, 0, 1));
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}

		try {
			this.setter = clz.getDeclaredMethod("set" + StringUtils.toUpperCase(name, 0, 1), type);
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}

		if (ClassUtils.isBooleanType(type)) {
			String methodNameSuffix = name;
			if (name.startsWith("is")) {
				Logger.warn("FieldInfo", "Boolean类型的字段不应该以is开头,class:" + clz.getName() + ",field:" + name);
				methodNameSuffix = name.substring(2);
			}
			methodNameSuffix = StringUtils.toUpperCase(name, 0, 1);

			if (this.getter == null) {
				try {
					this.getter = clz.getDeclaredMethod("is" + methodNameSuffix);
				} catch (NoSuchMethodException e) {
				} catch (SecurityException e) {
				}
			}

			if (this.setter == null) {
				try {
					this.setter = clz.getDeclaredMethod("set" + methodNameSuffix, type);
				} catch (NoSuchMethodException e) {
				} catch (SecurityException e) {
				}
			}
		}

		if (this.getter != null) {
			this.getter.setAccessible(true);
		}

		if (this.setter != null) {
			this.setter.setAccessible(true);
		}
	}

	public String getName() {
		return name;
	}

	public Field getField() {
		return field;
	}

	public Class<?> getType() {
		return type;
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
}

package shuchaowen.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

		// GET
		if ((boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) && name.startsWith("is")) {
			String methodName;
			if(name.startsWith("is")){
				methodName = name;
			}else{
				methodName = "is" + StringUtils.toUpperCase(name, 0, 1);
			}
			try {
				this.getter = clz.getDeclaredMethod(methodName);
			} catch (NoSuchMethodException e) {
			} catch (SecurityException e) {
			}
		} else {
			try {
				this.getter = clz.getDeclaredMethod("get" + StringUtils.toUpperCase(name, 0, 1));
			} catch (NoSuchMethodException e) {
			} catch (SecurityException e) {
			}
		}

		// SET
		if ((boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) && name.startsWith("is")) {
			try {
				this.setter = clz.getDeclaredMethod("set" + name.substring(2), type);
			} catch (NoSuchMethodException e) {
			} catch (SecurityException e) {
			}
		} else {
			try {
				this.setter = clz.getDeclaredMethod("set" + StringUtils.toUpperCase(name, 0, 1), type);
			} catch (NoSuchMethodException e) {
			} catch (SecurityException e) {
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
		if (getGetter() == null) {
			return forceGet(obj);
		} else {
			return getGetter().invoke(obj);
		}
	}

	public void set(Object obj, Object value)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (getSetter() == null) {
			forceSet(obj, value);
		} else {
			getSetter().invoke(obj, value);
		}
	}
	
	/**
	 * 不调用get方法直接获取值
	 * @param obj
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Object forceGet(Object obj) throws IllegalArgumentException, IllegalAccessException{
		return field.get(obj);
	}
	
	/**
	 * 不调用set方法直接设置值
	 * @param obj
	 * @param value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void forceSet(Object obj, Object value) throws IllegalArgumentException, IllegalAccessException{
		field.set(obj, value);
	}
}

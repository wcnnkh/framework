package scw.core.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class DefaultFieldDefinition implements FieldDefinition {
	private final Field field;
	private final Method getter;
	private final Method setter;

	public DefaultFieldDefinition(Class<?> clz, Field field, boolean sup, boolean getter, boolean setter) {
		this.field = field;
		this.getter = getter ? ReflectUtils.getGetterMethod(clz, field, sup) : null;
		this.setter = setter ? ReflectUtils.getSetterMethod(clz, field, sup) : null;
	}

	public Field getField() {
		return field;
	}

	public Object get(Object obj) throws Exception {
		if (getter == null) {
			return field.get(obj);
		} else {
			return getter.invoke(obj);
		}
	}

	public void set(Object obj, Object value) throws Exception {
		if (setter == null) {
			field.set(obj, value);
		} else {
			setter.invoke(obj, value);
		}
	}

}

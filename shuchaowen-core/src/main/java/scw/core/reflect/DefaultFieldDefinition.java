package scw.core.reflect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import scw.core.annotation.AnnotatedElementUtils;

public final class DefaultFieldDefinition implements FieldDefinition {
	private Class<?> clz;
	private final Field field;
	private final Method getter;
	private final Method setter;
	private final AnnotatedElement annotatedElement;

	/**
	 * 最完整的缓存
	 * 
	 * @param clz
	 * @param field
	 */
	public DefaultFieldDefinition(Class<?> clz, Field field) {
		this(clz, field, true, true, true);
	}

	public DefaultFieldDefinition(Class<?> clz, Field field, boolean getter, boolean setter, boolean annotationCache) {
		this.clz = clz;
		this.field = field;
		this.getter = getter ? ReflectionUtils.getGetterMethod(clz, field) : null;
		this.setter = setter ? ReflectionUtils.getSetterMethod(clz, field) : null;
		this.annotatedElement = annotationCache? AnnotatedElementUtils.forAnnotations(field.getDeclaredAnnotations()):field;
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
	
	public AnnotatedElement getAnnotatedElement() {
		return annotatedElement;
	}

	public String getName() {
		return field.getName();
	}

	public Class<?> getDeclaringClass() {
		return clz;
	}
}

package scw.core.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DefaultFieldDefinition implements FieldDefinition {
	private final Field field;
	private final Method getter;
	private final Method setter;
	private Map<Class<? extends Annotation>, Annotation> annnotationMap;
	private final boolean annotationCache;

	/**
	 * 最完整的缓存
	 * 
	 * @param clz
	 * @param field
	 */
	public DefaultFieldDefinition(Class<?> clz, Field field) {
		this(clz, field, true, true, true, true);
	}

	@SuppressWarnings("unchecked")
	public DefaultFieldDefinition(Class<?> clz, Field field, boolean sup,
			boolean getter, boolean setter, boolean annotationCache) {
		this.field = field;
		this.getter = getter ? ReflectUtils.getGetterMethod(clz, field, sup)
				: null;
		this.setter = setter ? ReflectUtils.getSetterMethod(clz, field, sup)
				: null;

		this.annotationCache = annotationCache;
		if (annotationCache) {
			Annotation[] annotations = field.getAnnotations();
			if (annotations == null) {
				annnotationMap = Collections.EMPTY_MAP;
			} else {
				annnotationMap = new HashMap<Class<? extends Annotation>, Annotation>(
						annotations.length, 1);
				for (Annotation annotation : annotations) {
					annnotationMap.put(annotation.annotationType(), annotation);
				}
			}
		}
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

	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return (T) (annotationCache ? annnotationMap.get(type) : field
				.getAnnotation(type));
	}

	public Class<?> getType() {
		return field.getType();
	}

	public String getName() {
		return field.getName();
	}

	public int getModifiers() {
		return field.getModifiers();
	}

	public boolean isStatic() {
		return Modifier.isStatic(field.getModifiers());
	}

	public boolean isTransient() {
		return Modifier.isTransient(field.getModifiers());
	}
}

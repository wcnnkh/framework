package scw.orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import scw.core.reflect.AnnotationFactory;
import scw.core.reflect.ReflectionUtils;
import scw.core.reflect.SimpleAnnotationFactory;

public class FieldColumn implements Column {
	private final Field field;
	private final AnnotationFactory annotationFactory;
	private final Method getterMethod;
	private final Method setterMethod;
	private final Class<?> clazz;

	public FieldColumn(Class<?> clazz, Field field) {
		this.clazz = clazz;
		this.field = field;
		this.annotationFactory = new SimpleAnnotationFactory(field);
		this.getterMethod = ReflectionUtils.getGetterMethod(clazz, field);
		this.setterMethod = ReflectionUtils.getSetterMethod(clazz, field);
	}

	public final <T extends Annotation> T getAnnotation(Class<T> type) {
		return annotationFactory.getAnnotation(type);
	}

	public final Field getField() {
		return field;
	}

	public final Object get(Object obj) throws Exception {
		return getterMethod == null ? field.get(obj) : getterMethod.invoke(obj);
	}

	public final void set(Object obj, Object value) throws Exception {
		if (setterMethod == null) {
			field.set(obj, value);
		} else {
			setterMethod.invoke(obj, value);
		}
	}

	public Class<?> getDeclaringClass() {
		return clazz;
	}

	public String getName() {
		return field.getName();
	}

	public Method getGetterMethod() {
		return getterMethod;
	}

	public Method getSetterMethod() {
		return setterMethod;
	}
}

package scw.orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import scw.core.reflect.AnnotationFactory;
import scw.core.reflect.ReflectionUtils;
import scw.core.reflect.SimpleAnnotationFactory;

public class FieldColumn extends AbstractColumn {
	private final Field field;
	private final AnnotationFactory annotationFactory;
	private final Method getter;
	private final Method setter;
	private final Class<?> clazz;

	public FieldColumn(Class<?> clazz, Field field) {
		this.clazz = clazz;
		this.field = field;
		this.annotationFactory = new SimpleAnnotationFactory(field);
		this.getter = ReflectionUtils.getGetterMethod(clazz, field);
		this.setter = ReflectionUtils.getSetterMethod(clazz, field);
	}

	public final <T extends Annotation> T getAnnotation(Class<T> type) {
		return annotationFactory.getAnnotation(type);
	}

	public final Field getField() {
		return field;
	}

	public Class<?> getDeclaringClass() {
		return clazz;
	}

	public String getName() {
		return field.getName();
	}

	public Method getGetter() {
		return getter;
	}

	public Method getSetter() {
		return setter;
	}
}

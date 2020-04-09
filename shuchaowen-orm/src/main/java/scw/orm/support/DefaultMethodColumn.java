package scw.orm.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedList;

import scw.core.annotation.AnnotationFactory;
import scw.core.annotation.MultipleAnnotationFactory;
import scw.core.annotation.SimpleAnnotationFactory;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.StringUtils;
import scw.lang.UnsupportedException;
import scw.orm.AbstractColumn;
import scw.orm.MethodColumn;
import scw.orm.ORMException;

public class DefaultMethodColumn extends AbstractColumn implements MethodColumn {
	private Method getter;
	private Method setter;
	private Field field;
	private String name;
	private AnnotationFactory annotationFactory;

	public DefaultMethodColumn(Class<?> declaringClass, Method getter, Method setter, Field field, String name) {
		super(declaringClass);
		this.getter = getter;
		this.setter = setter;
		this.field = field;
		this.name = name;
		LinkedList<AnnotationFactory> annotationFactories = new LinkedList<AnnotationFactory>();
		if (field != null) {
			ReflectionUtils.setAccessibleField(field);
			annotationFactories.add(new SimpleAnnotationFactory(field));
		}

		if (setter != null) {
			ReflectionUtils.setAccessibleMethod(setter);
			annotationFactories.add(new SimpleAnnotationFactory(setter));
		}

		if (getter != null) {
			ReflectionUtils.setAccessibleMethod(getter);
			annotationFactories.add(new SimpleAnnotationFactory(getter));
		}

		this.annotationFactory = new MultipleAnnotationFactory(annotationFactories);
		scw.orm.annotation.ColumnName columnName = getAnnotation(scw.orm.annotation.ColumnName.class);
		if (columnName != null && !StringUtils.isEmpty(columnName.value())) {
			this.name = columnName.value();
		}
	}

	public Field getField() {
		return field;
	}

	public Class<?> getType() {
		if (field != null) {
			return field.getType();
		}

		if (getter != null) {
			return getter.getReturnType();
		}

		if (setter != null) {
			return setter.getParameterTypes()[0];
		}

		throw new UnsupportedException(getClass() + ", name=" + getName());
	}

	public Type getGenericType() {
		if (field != null) {
			return field.getGenericType();
		}

		if (getter != null) {
			return getter.getGenericReturnType();
		}

		if (setter != null) {
			return setter.getGenericParameterTypes()[0];
		}

		throw createNotSupportException();
	}

	public String getName() {
		return name;
	}

	public boolean isSupportGet() {
		return getter != null || field != null;
	}

	public Object get(Object obj) throws ORMException {
		if (getter != null) {
			try {
				return getter.invoke(obj);
			} catch (Exception e) {
				throw createSetterORMException(e);
			}
		}

		if (field != null) {
			try {
				return field.get(obj);
			} catch (Exception e) {
				throw createSetterORMException(e);
			}
		}
		throw createNotSupportException();
	}

	public boolean isSupportSet() {
		return field != null && setter != null;
	}

	public void set(Object obj, Object value) throws ORMException {
		if (setter != null) {
			try {
				setter.invoke(obj, value);
			} catch (Exception e) {
				throw createSetterORMException(e);
			}
		}

		if (field != null) {
			try {
				field.set(obj, value);

			} catch (Exception e) {
				throw createSetterORMException(e);
			}
		}
		throw createNotSupportException();
	}

	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return annotationFactory.getAnnotation(type);
	}

	public Method getGetter() {
		return getter;
	}

	public Method getSetter() {
		return setter;
	}

}

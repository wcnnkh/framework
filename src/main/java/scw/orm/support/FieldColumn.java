package scw.orm.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import scw.core.reflect.AnnotationFactory;
import scw.core.reflect.SimpleAnnotationFactory;
import scw.core.utils.StringUtils;
import scw.lang.Description;
import scw.orm.Column;
import scw.orm.ORMException;

public class FieldColumn implements Column {
	private Field field;
	private Class<?> clazz;
	private String name;
	private AnnotationFactory annotationFactory;

	public FieldColumn(Class<?> clazz, Field field) {
		this.clazz = clazz;
		this.field = field;
		this.annotationFactory = new SimpleAnnotationFactory(field);
		scw.orm.annotation.ColumnName columnName = getAnnotation(scw.orm.annotation.ColumnName.class);
		if (columnName != null && !StringUtils.isEmpty(columnName.value())) {
			this.name = columnName.value();
		}
	}

	public final Field getField() {
		return field;
	}

	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return annotationFactory.getAnnotation(type);
	}

	public Object get(Object obj) throws ORMException {
		// 默认不调用get方法
		try {
			return field.get(obj);
		} catch (Exception e) {
			throw new ORMException("[ORM getter failed] - column [" + getName() + "]", e);
		}
	}

	public void set(Object obj, Object value) throws ORMException {
		// 如果存在set方法，调用setter方法
		try {
			field.set(obj, value);
		} catch (Exception e) {
			throw new ORMException("[ORM setter failed] - column [" + getName() + "]", e);
		}
	}

	public Type getGenericType() {
		return field.getGenericType();
	}

	public Class<?> getType() {
		return field.getType();
	}

	public String getName() {
		return name == null ? field.getName() : name;
	}

	public Class<?> getDeclaringClass() {
		return clazz;
	}

	public String getDescription() {
		Description description = getAnnotation(Description.class);
		return description == null ? null : description.value();
	}
}

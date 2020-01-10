package scw.orm.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

import scw.core.reflect.AnnotationFactory;
import scw.core.reflect.MultipleAnnotationFactory;
import scw.core.reflect.ReflectionUtils;
import scw.core.reflect.SimpleAnnotationFactory;
import scw.core.utils.StringUtils;
import scw.orm.AbstractColumn;
import scw.orm.Column;
import scw.orm.ORMException;

public class DefaultFieldColumn extends AbstractColumn implements Column {
	private Field field;
	private String name;
	private AnnotationFactory annotationFactory;
	private Method getter;
	private Method setter;

	public DefaultFieldColumn(Class<?> clazz, Field field, boolean getter, boolean setter) {
		super(clazz);
		this.field = field;
		this.annotationFactory = new SimpleAnnotationFactory(field);
		if (getter) {
			this.getter = ReflectionUtils.getGetterMethod(clazz, field);
			if (this.getter != null) {
				this.annotationFactory = new MultipleAnnotationFactory(
						Arrays.asList(new SimpleAnnotationFactory(this.getter), this.annotationFactory));
			}
		}

		if (setter) {
			this.setter = ReflectionUtils.getSetterMethod(clazz, field);
			if (this.setter != null) {
				this.annotationFactory = new MultipleAnnotationFactory(
						Arrays.asList(new SimpleAnnotationFactory(this.setter), this.annotationFactory));
			}
		}

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
		try {
			if (getter == null) {// 不使用三元表达式是为了方便找到错误在那一行
				return field.get(obj);
			} else {
				return getter.invoke(obj);
			}
		} catch (Exception e) {
			throw createGetterORMException(e);
		}
	}

	public void set(Object obj, Object value) throws ORMException {
		// 如果存在set方法，调用setter方法
		try {
			if (setter == null) {
				field.set(obj, value);
			} else {
				setter.invoke(obj, value);
			}
		} catch (Exception e) {
			throw createSetterORMException(e);
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

	public boolean isSupportGet() {
		return true;
	}

	public boolean isSupportSet() {
		return true;
	}
}

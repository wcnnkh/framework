package scw.orm.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import scw.core.annotation.MultiAnnotatedElement;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.StringUtils;
import scw.lang.NotSupportedException;
import scw.orm.AbstractColumn;
import scw.orm.MethodColumn;
import scw.orm.ORMException;

public class DefaultMethodColumn extends AbstractColumn implements MethodColumn {
	private Method getter;
	private Method setter;
	private Field field;
	private String name;
	private AnnotatedElement annotatedElement;

	public DefaultMethodColumn(Class<?> declaringClass, Method getter, Method setter, Field field, String name) {
		super(declaringClass);
		this.getter = getter;
		this.setter = setter;
		this.field = field;
		this.name = name;
		if (field != null) {
			ReflectionUtils.setAccessibleField(field);
		}

		if (setter != null) {
			ReflectionUtils.setAccessibleMethod(setter);
		}

		if (getter != null) {
			ReflectionUtils.setAccessibleMethod(getter);
		}
		
		this.annotatedElement = MultiAnnotatedElement.forAnnotatedElements(true, field, getter, setter);
		scw.orm.annotation.ColumnName columnName = getAnnotatedElement().getAnnotation(scw.orm.annotation.ColumnName.class);
		if (columnName != null && !StringUtils.isEmpty(columnName.value())) {
			this.name = columnName.value();
		}
	}
	
	public AnnotatedElement getAnnotatedElement() {
		return annotatedElement;
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

		throw new NotSupportedException(getClass() + ", name=" + getName());
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

	public Method getGetter() {
		return getter;
	}

	public Method getSetter() {
		return setter;
	}

}

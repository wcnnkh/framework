package io.basc.framework.mapper;

import io.basc.framework.core.annotation.AnnotatedElementWrapper;
import io.basc.framework.core.annotation.AnnotationArrayAnnotatedElement;
import io.basc.framework.core.annotation.MultiAnnotatedElement;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.NestedExceptionUtils;
import io.basc.framework.lang.NotSupportedException;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class AbstractFieldDescriptor extends AnnotatedElementWrapper<AnnotatedElement>
		implements FieldDescriptor {
	private final Class<?> sourceClass;
	private final Field field;
	private final Method method;

	public AbstractFieldDescriptor(Class<?> sourceClass, Field field, Method method) {
		super(new AnnotationArrayAnnotatedElement(MultiAnnotatedElement.forAnnotatedElements(method, field)));
		this.sourceClass = sourceClass;
		this.field = field;
		this.method = method;
	}

	public java.lang.reflect.Field getField() {
		return field;
	}

	public Method getMethod() {
		return method;
	}

	public int getModifiers() {
		Method method = getMethod();
		Field field = getField();
		if (method != null && field != null) {
			return method.getModifiers() | field.getModifiers();
		}

		if (method != null) {
			return method.getModifiers();
		}

		if (field != null) {
			return field.getModifiers();
		}
		throw createNotSupportException();
	}

	protected NotSupportedException createNotSupportException() {
		return new NotSupportedException("class=[" + sourceClass + "] field [" + getName() + "]");
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof FieldDescriptor) {
			// 这里并不需要判断泛型类型，因为如果set/get方法的"参数类型/返回类型"一样时就是方法的重载
			return getType().equals(((FieldDescriptor) obj).getType())
					&& ((FieldDescriptor) obj).getName().equals(getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode() + getType().hashCode();
	}

	@Override
	public String toString() {
		if (field == null && method == null) {
			return "declaringClass [" + sourceClass + "] name [" + getName() + "]";
		}

		StringBuilder sb = new StringBuilder();
		if (field != null) {
			sb.append("field[").append(field).append("]");
		}

		if (method != null) {
			if (sb.length() != 0) {
				sb.append(" ");
			}
			sb.append("method[").append(method).append("]");
		}
		return sb.toString();
	}

	public Object get(Object instance) {
		Method method = getMethod();
		if (method != null) {
			try {
				return ReflectionUtils.invoke(method, Modifier.isStatic(method.getModifiers()) ? null : instance);
			} catch (Exception e) {
				throw new RuntimeException(toString() + " instance [" + instance + "]",
						NestedExceptionUtils.excludeInvalidNestedExcpetion(e));
			}
		}

		java.lang.reflect.Field field = getField();
		if (field != null) {
			ReflectionUtils.makeAccessible(field);
			try {
				return ReflectionUtils.get(field, Modifier.isStatic(field.getModifiers()) ? null : instance);
			} catch (Exception e) {
				throw new RuntimeException(toString() + " instance [" + instance + "]",
						NestedExceptionUtils.excludeInvalidNestedExcpetion(e));
			}
		}
		throw createNotSupportException();
	}

	public void set(Object instance, Object value) {
		Method method = getMethod();
		if (method != null) {
			try {
				ReflectionUtils.invoke(method, Modifier.isStatic(method.getModifiers()) ? null : instance, value);
			} catch (Exception e) {
				throw new RuntimeException(toString() + " instance [" + instance + "] value [" + value + "]",
						NestedExceptionUtils.excludeInvalidNestedExcpetion(e));
			}
			return;
		}

		java.lang.reflect.Field field = getField();
		if (field != null) {
			ReflectionUtils.makeAccessible(field);
			try {
				ReflectionUtils.set(field, Modifier.isStatic(field.getModifiers()) ? null : instance, value);
			} catch (Exception e) {
				throw new RuntimeException(toString() + " instance [" + instance + "] value [" + value + "]",
						NestedExceptionUtils.excludeInvalidNestedExcpetion(e));
			}
			return;
		}
		throw createNotSupportException();
	}
}

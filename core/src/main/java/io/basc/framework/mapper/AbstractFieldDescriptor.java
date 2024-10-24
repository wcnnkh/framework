package io.basc.framework.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.basc.framework.core.annotation.MultiAnnotatedElement;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.NestedExceptionUtils;
import io.basc.framework.lang.UnsupportedException;

public abstract class AbstractFieldDescriptor extends MultiAnnotatedElement implements FieldDescriptor {
	private final Class<?> sourceClass;
	private final Field field;
	private final Method method;

	public AbstractFieldDescriptor(Class<?> sourceClass, Field field, Method method) {
		super(method, field);
		this.sourceClass = sourceClass;
		this.field = field;
		this.method = method;
	}

	@Override
	public Class<?> getDeclaringClass() {
		return sourceClass;
	}

	public java.lang.reflect.Field getField() {
		return field;
	}

	public Method getMethod() {
		return method;
	}

	@Override
	public boolean isSynthetic() {
		Method method = getMethod();
		if (method != null && field != null) {
			return method.isSynthetic() || field.isSynthetic();
		}

		if (method != null) {
			return method.isSynthetic();
		}

		if (field != null) {
			return field.isSynthetic();
		}
		return false;
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
		return 0;
	}

	protected UnsupportedException createNotSupportException() {
		return new UnsupportedException("class=[" + sourceClass + "] field [" + getName() + "]");
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
			return "declaringClass [" + getDeclaringClass() + "] name [" + getName() + "]";
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

		Field field = getField();
		if (field != null) {
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

		Field field = getField();
		if (field != null) {
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

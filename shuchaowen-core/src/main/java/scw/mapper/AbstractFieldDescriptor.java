package scw.mapper;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import scw.core.annotation.MultiAnnotatedElement;
import scw.core.reflect.SerializableField;
import scw.core.reflect.SerializableMethod;
import scw.lang.NotSupportedException;

public abstract class AbstractFieldDescriptor
		implements FieldDescriptor {
	private static final long serialVersionUID = 1L;
	private final Class<?> declaringClass;
	private final SerializableField field;
	private final SerializableMethod method;
	private final AnnotatedElement annotatedElement;

	public AbstractFieldDescriptor(Class<?> declaringClass, Field field, Method method) {
		this.declaringClass = declaringClass;
		this.annotatedElement = MultiAnnotatedElement.forAnnotatedElements(
				true, method, field);
		this.field = field == null ? null : new SerializableField(
				declaringClass, field);
		this.method = method == null ? null : new SerializableMethod(
				declaringClass, method);
	}
	
	public AnnotatedElement getAnnotatedElement() {
		return annotatedElement;
	}

	public java.lang.reflect.Field getField() {
		return field == null ? null : field.getField();
	}

	public Method getMethod() {
		return method == null ? null : method.getMethod();
	}
	
	public int getModifiers() {
		Method method = getMethod();
		Field field = getField();
		if(method != null && field != null){
			return method.getModifiers() | field.getModifiers();
		}
		
		if(method != null){
			return method.getModifiers();
		}
		
		if(field != null){
			return field.getModifiers();
		}
		throw createNotSupportException();
	}

	protected NotSupportedException createNotSupportException() {
		return new NotSupportedException("class=["
				+ getDeclaringClass() + "] column [" + getName() + "]");
	}
	
	public Class<?> getDeclaringClass() {
		return declaringClass;
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
			return ((FieldDescriptor) obj).getType() == getType()
					&& ((FieldDescriptor) obj).getName().equals(
							getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode() + getType().hashCode();
	}
	
	@Override
	public String toString() {
		return "declaringClass [" + getDeclaringClass() + "] name [" + getName() + "]";
	}
}

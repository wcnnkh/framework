package scw.core.reflect;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.core.annotation.MultiAnnotatedElement;

abstract class AbstractGetterSetter extends AbstractFieldMetadata
		implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private final SerializableField field;
	private final SerializableMethod method;
	private final AnnotatedElement annotatedElement;

	public AbstractGetterSetter(Class<?> declaringClass, String name,
			Field field, Method method) {
		super(declaringClass);
		this.name = name;
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
	
	public Object get(Object instance) throws Exception {
		Method method = getMethod();
		if (method != null) {
			ReflectionUtils.setAccessibleMethod(method);
			return method
					.invoke(Modifier.isStatic(method.getModifiers()) ? null
							: instance);
		}

		java.lang.reflect.Field field = getField();
		if (field != null) {
			ReflectionUtils.setAccessibleField(field);
			return field.get(Modifier.isStatic(field.getModifiers()) ? null
					: instance);
		}
		throw createNotSupportException();
	}

	public void set(Object instance, Object value) throws Exception {
		Method method = getMethod();
		if (method != null) {
			ReflectionUtils.setAccessibleMethod(method);
			method.invoke(Modifier.isStatic(method.getModifiers()) ? null
					: instance, value);
			return ;
		}

		java.lang.reflect.Field field = getField();
		if (field != null) {
			ReflectionUtils.setAccessibleField(field);
			field.set(
					Modifier.isStatic(field.getModifiers()) ? null : instance,
					value);
			return ;
		}
		throw createNotSupportException();
	}

	public String getName() {
		return name;
	}
}

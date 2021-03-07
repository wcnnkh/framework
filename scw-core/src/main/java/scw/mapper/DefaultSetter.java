package scw.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.annotation.AnnotationUtils;
import scw.core.utils.ArrayUtils;
import scw.util.Supplier;

public class DefaultSetter extends AbstractFieldDescriptor implements Setter{
	private static final long serialVersionUID = 1L;
	private final String name;
	private final boolean nullable;

	public DefaultSetter(Class<?> declaringClass, String name, Field field,
			Method method) {
		super(declaringClass, field, method);
		this.name = name;
		
		AnnotatedElement setterParameterAnnotatedElement = null;
		if(method != null){
			Annotation[][] annotations = method.getParameterAnnotations();
			setterParameterAnnotatedElement = ArrayUtils.isEmpty(annotations)? null:AnnotatedElementUtils.forAnnotations(annotations[0]);
		}
		
		if(setterParameterAnnotatedElement == null){
			this.nullable = AnnotationUtils.isNullable(getAnnotatedElement(), false);
		}else{
			this.nullable = AnnotationUtils.isNullable(setterParameterAnnotatedElement, new Supplier<Boolean>() {
				
				public Boolean get() {
					return AnnotationUtils.isNullable(getAnnotatedElement(), false);
				}
			}).get();
		}
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isNullable() {
		return nullable;
	}

	public Class<?> getType() {
		Method method = getMethod();
		if (method != null) {
			return method.getParameterTypes()[0];
		}

		Field field = getField();
		if (field != null) {
			return field.getType();
		}
		throw createNotSupportException();
	}

	public Type getGenericType() {
		Method method = getMethod();
		if (method != null) {
			return method.getGenericParameterTypes()[0];
		}

		Field field = getField();
		if (field != null) {
			return field.getGenericType();
		}
		throw createNotSupportException();
	}
}

package scw.servlet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public interface MethodParameter {
	Method getMethod();
	
	Annotation getAnnotation(Class<? extends Annotation> type);
	
	Class<?> getType();
	
	Type getGenericType();
	
	String getName();
	
	int getIndex();
}

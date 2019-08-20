package scw.servlet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ParameterDefinition {
	int getParameterCount();
	
	String getName();

	Annotation getAnnotation(Class<? extends Annotation> type);

	Class<?> getType();

	Type getGenericType();
	
	int getIndex();
}

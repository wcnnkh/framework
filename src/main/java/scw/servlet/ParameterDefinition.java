package scw.servlet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ParameterDefinition {
	int getParameterCount();
	
	String getName();

	<T extends Annotation> T getAnnotation(Class<T> type);

	Class<?> getType();

	Type getGenericType();
	
	int getIndex();
}

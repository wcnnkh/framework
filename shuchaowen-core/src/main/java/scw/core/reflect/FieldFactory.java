package scw.core.reflect;

import java.util.Collection;

public interface FieldFactory {
	Collection<Field> getFields(Class<?> clazz);
	
	Collection<FieldContext> getFieldContexts(Class<?> clazz,
			FieldContext parentContext, FieldContextFilter filter);
	
	FieldContext getFieldContext(Class<?> clazz,
			FieldContext parentContext, FieldContextFilter filter);
}
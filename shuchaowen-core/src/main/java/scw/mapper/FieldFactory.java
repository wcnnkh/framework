package scw.mapper;

import java.util.Collection;
import java.util.LinkedList;

public interface FieldFactory {
	Collection<Field> getFields(Class<?> clazz);
	
	LinkedList<FieldContext> getFieldContexts(Class<?> clazz,
			FieldContext parentContext, FieldContextFilter filter, FieldFilterType ...fieldFilterTypes);
	
	FieldContext getFieldContext(Class<?> clazz,
			FieldContext parentContext, FieldContextFilter filter, FieldFilterType ...fieldFilterTypes);
	
	FieldContext getFieldContext(Class<?> clazz, String name, FieldFilterType ...fieldFilterTypes);
	
	FieldContext getFieldContext(Class<?> clazz, String name, FieldContext parentContext, FieldFilterType ...fieldFilterTypes);
}
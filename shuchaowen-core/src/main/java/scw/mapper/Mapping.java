package scw.mapper;

public interface Mapping extends FieldContextFilter{
	
	<T> T newInstance(Class<? extends T> entityClass);
	
	Object mapping(Class<?> entityClass, FieldContext fieldContext, FieldFactory fieldFactory);
}

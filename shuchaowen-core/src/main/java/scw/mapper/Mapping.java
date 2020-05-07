package scw.mapper;

public interface Mapping extends FieldContextFilter{
	
	<T> T newInstance(Class<? extends T> entityClass) throws Exception;
	
	Object mapping(Class<?> entityClass, FieldContext fieldContext, Mapper fieldFactory) throws Exception;
}

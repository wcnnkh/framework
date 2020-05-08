package scw.mapper;

public interface Mapping extends FieldFilter{
	
	<T> T newInstance(Class<? extends T> entityClass) throws Exception;
	
	Object mapping(Class<?> entityClass, Field field, Mapper mapper) throws Exception;
}

package scw.mapper;

public interface Mapper extends FieldContextFilter{
	boolean isEntity(FieldDescriptor fieldDescriptor);
	
	<T> T mapping(Class<? extends T> entityClass, EntityMapping entityMapping, FieldFactory fieldFactory) throws Exception;
}

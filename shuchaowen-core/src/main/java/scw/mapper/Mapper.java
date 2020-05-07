package scw.mapper;

public interface Mapper extends EntityResolver{
	<T> T mapping(Class<? extends T> entityClass, EntityMapping entityMapping, FieldFactory fieldFactory) throws Exception;
}

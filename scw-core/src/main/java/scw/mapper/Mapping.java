package scw.mapper;

public interface Mapping extends FieldFilter {
	<T> T mapping(Class<? extends T> entityClass, Fields fields, Mapper mapper) throws Exception;
}

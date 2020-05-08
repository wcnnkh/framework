package scw.mapper;

import java.util.Collection;

public interface Mapping extends FieldFilter {
	<T> T mapping(Class<? extends T> entityClass, Collection<Field> fields, Mapper mapper) throws Exception;
}

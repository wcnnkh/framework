package scw.mapper;

import scw.util.Accept;

public interface Mapping extends Accept<Field> {
	<T> T mapping(Class<T> entityClass, Fields fields, Mapper mapper);
}

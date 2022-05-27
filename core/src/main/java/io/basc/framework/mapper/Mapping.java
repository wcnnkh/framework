package io.basc.framework.mapper;

import io.basc.framework.util.Accept;

public interface Mapping extends Accept<Field> {
	<T> T mapping(Class<T> entityClass, Fields fields);
}

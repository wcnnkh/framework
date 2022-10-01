package io.basc.framework.mapper;

import java.util.function.Predicate;

public interface Mapping extends Predicate<Field> {
	<T> T mapping(Class<T> entityClass, Fields fields);
}

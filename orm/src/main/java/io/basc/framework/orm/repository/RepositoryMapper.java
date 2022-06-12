package io.basc.framework.orm.repository;

import io.basc.framework.mapper.ObjectMapper;

public interface RepositoryMapper<S, E extends Throwable> extends RepositoryResolver, ObjectMapper<S, E> {

	@Override
	default boolean isEntity(Class<?> entityClass) {
		return RepositoryResolver.super.isEntity(entityClass);
	}
}

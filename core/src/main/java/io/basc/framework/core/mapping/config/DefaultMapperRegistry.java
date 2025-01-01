package io.basc.framework.core.mapping.config;

import io.basc.framework.core.mapping.Mapper;
import io.basc.framework.util.spi.ServiceMap;
import lombok.NonNull;

public class DefaultMapperRegistry<S, E extends Throwable> extends ServiceMap<Mapper<S, ?, E>>
		implements MapperFactory<S, E> {
	@SuppressWarnings("unchecked")
	@Override
	public <T> Mapper<S, T, E> getMapper(@NonNull Class<? extends T> requiredType) {
		return (Mapper<S, T, E>) search(requiredType).first();
	}
}

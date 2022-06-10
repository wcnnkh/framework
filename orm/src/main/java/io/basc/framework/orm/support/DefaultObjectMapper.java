package io.basc.framework.orm.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.mapper.ObjectAccessFactory;
import io.basc.framework.mapper.ObjectAccessFactoryRegistry;
import io.basc.framework.orm.repository.AbstractRepositoryObjectMapper;

public class DefaultObjectMapper<E extends Throwable> extends AbstractRepositoryObjectMapper<Object, E>
		implements ObjectAccessFactoryRegistry<E> {
	private final Map<Class<?>, ObjectAccessFactory<?, ? extends E>> map = new ConcurrentHashMap<Class<?>, ObjectAccessFactory<?, ? extends E>>();

	@Override
	public boolean isObjectAccessFactoryRegistred(Class<?> type) {
		return map.containsKey(type);
	}

	@Override
	public <S> void registerObjectAccessFactory(Class<S> type, ObjectAccessFactory<? super S, ? extends E> factory) {
		map.put(type, factory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S> ObjectAccessFactory<S, E> getObjectAccessFactory(Class<? extends S> type) {
		return (ObjectAccessFactory<S, E>) map.get(type);
	}
}

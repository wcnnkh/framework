package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;

public interface ObjectAccessFactoryRegistry<E extends Throwable> extends ObjectAccessFactory<Object, E> {
	default boolean isObjectAccessFactoryRegistred(Class<?> type) {
		return getObjectAccessFactory(type) != null;
	}

	<S> void registerObjectAccessFactory(Class<S> type, ObjectAccessFactory<? super S, ? extends E> factory);

	<S> ObjectAccessFactory<S, E> getObjectAccessFactory(Class<? extends S> type);

	@Override
	default ObjectAccess<E> getObjectAccess(Object source, TypeDescriptor sourceType) throws E {
		if (source == null || sourceType == null) {
			return null;
		}

		ObjectAccessFactory<Object, E> factory = getObjectAccessFactory(sourceType.getType());
		if (factory == null) {
			return null;
		}
		return factory.getObjectAccess(source, sourceType);
	}
}

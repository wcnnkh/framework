package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;

public interface ObjectAccessFactoryRegistry extends ObjectAccessFactory<Object> {
	default boolean isObjectAccessFactoryRegistred(Class<?> type) {
		return getObjectAccessFactory(type) != null;
	}

	<S> void registerObjectAccessFactory(Class<S> type, ObjectAccessFactory<? super S> factory);

	<S> ObjectAccessFactory<S> getObjectAccessFactory(Class<? extends S> type);

	@Override
	default ObjectAccess getObjectAccess(Object source, TypeDescriptor sourceType) {
		if (source == null || sourceType == null) {
			return null;
		}

		ObjectAccessFactory<Object> factory = getObjectAccessFactory(sourceType.getType());
		if (factory == null) {
			return null;
		}
		return factory.getObjectAccess(source, sourceType);
	}
}

package io.basc.framework.data;

import io.basc.framework.data.template.TemporaryStorageTemplateWrapper;

public interface CompleteOperationsWrapper
		extends CompleteOperations, TemporaryStorageCasOperationsWrapper, TemporaryStorageTemplateWrapper {
	@Override
	CompleteOperations getSourceOperations();

	@Override
	default boolean setIfAbsent(String key, Object value) {
		return TemporaryStorageCasOperationsWrapper.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(String key, Object value) {
		return TemporaryStorageCasOperationsWrapper.super.setIfPresent(key, value);
	}

	@Override
	default void set(String key, Object value) {
		TemporaryStorageCasOperationsWrapper.super.set(key, value);
	}
}

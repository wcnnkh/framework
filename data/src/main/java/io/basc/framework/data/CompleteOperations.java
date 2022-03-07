package io.basc.framework.data;

import io.basc.framework.data.template.TemporaryStorageTemplate;

public interface CompleteOperations extends TemporaryStorageCasOperations, TemporaryStorageTemplate {

//	@Override
//	default void set(String key, Object value) {
//		TemporaryStorageCasOperations.super.set(key, value);
//	}
//
//	@Override
//	default boolean setIfAbsent(String key, Object value) {
//		return TemporaryStorageCasOperations.super.setIfAbsent(key, value);
//	}
//
//	@Override
//	default boolean setIfPresent(String key, Object value) {
//		return TemporaryStorageCasOperations.super.setIfPresent(key, value);
//	}
}

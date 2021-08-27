package io.basc.framework.mapper;

@FunctionalInterface
public interface FieldMetadataFactory {
	FieldMetadatas getFieldMetadatas(Class<?> clazz);
}

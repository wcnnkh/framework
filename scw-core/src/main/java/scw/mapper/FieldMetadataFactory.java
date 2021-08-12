package scw.mapper;

@FunctionalInterface
public interface FieldMetadataFactory {
	FieldMetadatas getFieldMetadatas(Class<?> clazz);
}

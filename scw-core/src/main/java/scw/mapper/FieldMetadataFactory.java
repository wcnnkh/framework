package scw.mapper;

import java.util.Collection;

public interface FieldMetadataFactory {
	Collection<FieldMetadata> getFieldMetadatas(Class<?> clazz);
}

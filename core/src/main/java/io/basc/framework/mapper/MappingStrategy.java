package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;

/**
 * 映射策略
 * 
 * @author wcnnkh
 */
public interface MappingStrategy {
	void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext, String name,
			ObjectAccess targetAccess, MappingContext targetContext) throws MappingException;

	void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext, Object target,
			TypeDescriptor targetType, MappingContext targetContext, Mapping<? extends FieldDescriptor> targetMapping,
			FieldDescriptor targetField) throws MappingException;

	void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType, MappingContext sourceContext,
			Mapping<? extends FieldDescriptor> sourceMapping, FieldDescriptor sourceField, ObjectAccess targetAccess,
			MappingContext targetContext) throws MappingException;

	void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType, MappingContext sourceContext,
			Mapping<? extends FieldDescriptor> sourceMapping, Object target, TypeDescriptor targetType,
			MappingContext targetContext, Mapping<? extends FieldDescriptor> targetMapping, FieldDescriptor targetField)
			throws MappingException;
}

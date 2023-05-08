package io.basc.framework.mapper;

import io.basc.framework.value.Value;

/**
 * 映射策略
 * 
 * @author wcnnkh
 */
public interface MappingStrategy {
	void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext, String name,
			ObjectAccess targetAccess, MappingContext targetContext) throws MappingException;

	void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext, Value target,
			MappingContext targetContext, Mapping<? extends Field> targetMapping, Field targetField)
			throws MappingException;

	void transform(ObjectMapper objectMapper, Value source, MappingContext sourceContext,
			Mapping<? extends Field> sourceMapping, Field sourceField, ObjectAccess targetAccess,
			MappingContext targetContext) throws MappingException;

	void transform(ObjectMapper objectMapper, Value source, MappingContext sourceContext,
			Mapping<? extends Field> sourceMapping, Value target, MappingContext targetContext,
			Mapping<? extends Field> targetMapping, Field targetField) throws MappingException;
}

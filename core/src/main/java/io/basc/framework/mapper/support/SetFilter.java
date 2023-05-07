package io.basc.framework.mapper.support;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingException;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.value.Value;

public interface SetFilter {
	<T extends Field> void set(ObjectMapper objectMapper, Value source, Mapping<? extends T> sourceMapping,
			T sourceField, Value target, Mapping<? extends T> targetMapping, T targetField, SetFilterChain chain)
			throws MappingException;

	<T extends Field> void set(ObjectMapper objectMapper, ObjectAccess sourceField, Value target,
			Mapping<? extends T> targetMapping, T targetField, SetFilterChain chain) throws MappingException;

	<T extends Field> void set(ObjectMapper objectMapper, Value source, Mapping<? extends T> sourceMapping,
			T sourceField, ObjectAccess targetAccess, SetFilterChain chain) throws MappingException;

	void set(ObjectMapper objectMapper, ObjectAccess sourceAccess, ObjectAccess targetAccess, String name,
			SetFilterChain chain) throws MappingException;
}

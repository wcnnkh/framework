package io.basc.framework.mapper.filter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingContext;
import io.basc.framework.mapper.MappingException;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.ObjectMapper;

/**
 * 映射策略拦截器
 * 
 * @see MappingStrategyChain
 * @author wcnnkh
 *
 */
public interface MappingStrategyFilter {
	void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext, String name,
			ObjectAccess targetAccess, MappingContext targetContext, MappingStrategy mappingStrategy)
			throws MappingException;

	void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext, Object target,
			TypeDescriptor targetType, MappingContext targetContext, Mapping<? extends Field> targetMapping,
			Field targetField, MappingStrategy mappingStrategy) throws MappingException;

	void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType, MappingContext sourceContext,
			Mapping<? extends Field> sourceMapping, Field sourceField, ObjectAccess targetAccess,
			MappingContext targetContext, MappingStrategy mappingStrategy) throws MappingException;

	void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType, MappingContext sourceContext,
			Mapping<? extends Field> sourceMapping, Object target, TypeDescriptor targetType,
			MappingContext targetContext, Mapping<? extends Field> targetMapping, Field targetField,
			MappingStrategy mappingStrategy) throws MappingException;
}

package io.basc.framework.mapper.filter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.access.ObjectAccess;
import io.basc.framework.mapper.entity.FieldDescriptor;
import io.basc.framework.mapper.entity.Mapping;
import io.basc.framework.mapper.entity.MappingContext;
import io.basc.framework.mapper.entity.MappingException;
import io.basc.framework.mapper.entity.MappingStrategy;

/**
 * 映射策略拦截器
 * 
 * @see MappingStrategyChain
 * @author wcnnkh
 *
 */
public interface MappingStrategyFilter {
	/**
	 * ObjectAccess的映射Filter
	 * 
	 * @param objectMapper
	 * @param sourceAccess
	 * @param sourceContext
	 * @param name
	 * @param targetAccess
	 * @param targetContext
	 * @param mappingStrategy
	 * @throws MappingException
	 */
	default void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			String name, ObjectAccess targetAccess, MappingContext targetContext, MappingStrategy mappingStrategy)
			throws MappingException {
		mappingStrategy.transform(objectMapper, sourceAccess, sourceContext, name, targetAccess, targetContext);
	}

	/**
	 * ObjectAccess到目标字段的映射Filter
	 * 
	 * @param objectMapper
	 * @param sourceAccess
	 * @param sourceContext
	 * @param target
	 * @param targetType
	 * @param targetContext
	 * @param targetMapping
	 * @param targetField
	 * @param mappingStrategy
	 * @throws MappingException
	 */
	default void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			Object target, TypeDescriptor targetType, MappingContext targetContext,
			Mapping<? extends FieldDescriptor> targetMapping, FieldDescriptor targetField, MappingStrategy mappingStrategy)
			throws MappingException {
		mappingStrategy.transform(objectMapper, sourceAccess, sourceContext, target, targetType, targetContext,
				targetMapping, targetField);
	}

	/**
	 * Field到ObjectAccess的映射Filter
	 * 
	 * @param objectMapper
	 * @param source
	 * @param sourceType
	 * @param sourceContext
	 * @param sourceMapping
	 * @param sourceField
	 * @param targetAccess
	 * @param targetContext
	 * @param mappingStrategy
	 * @throws MappingException
	 */
	default void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends FieldDescriptor> sourceMapping, FieldDescriptor sourceField,
			ObjectAccess targetAccess, MappingContext targetContext, MappingStrategy mappingStrategy)
			throws MappingException {
		mappingStrategy.transform(objectMapper, source, sourceType, sourceContext, sourceMapping, sourceField,
				targetAccess, targetContext);
	}

	/**
	 * 字段到字段之间的映射
	 * 
	 * @param objectMapper
	 * @param source
	 * @param sourceType
	 * @param sourceContext
	 * @param sourceMapping
	 * @param target
	 * @param targetType
	 * @param targetContext
	 * @param targetMapping
	 * @param targetField
	 * @param mappingStrategy
	 * @throws MappingException
	 */
	default void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends FieldDescriptor> sourceMapping, Object target,
			TypeDescriptor targetType, MappingContext targetContext, Mapping<? extends FieldDescriptor> targetMapping,
			FieldDescriptor targetField, MappingStrategy mappingStrategy) throws MappingException {
		mappingStrategy.transform(objectMapper, source, sourceType, sourceContext, sourceMapping, target, targetType,
				targetContext, targetMapping, targetField);
	}
}

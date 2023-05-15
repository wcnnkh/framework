package io.basc.framework.mapper.filter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Getter;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingContext;
import io.basc.framework.mapper.MappingException;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.Setter;
import io.basc.framework.util.PredicateRegistry;

/**
 * 通过参数描述来进行断言
 * 
 * @author wcnnkh
 *
 */
public class ParameterDescriptorFilter implements MappingStrategyFilter {
	private final PredicateRegistry<ParameterDescriptor> predicateRegistry = new PredicateRegistry<>();

	public PredicateRegistry<ParameterDescriptor> getPredicateRegistry() {
		return predicateRegistry;
	}

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			String name, ObjectAccess targetAccess, MappingContext targetContext, MappingStrategy mappingStrategy)
			throws MappingException {
		Parameter parameter = sourceAccess.get(name);
		if (parameter == null) {
			mappingStrategy.transform(objectMapper, sourceAccess, sourceContext, name, targetAccess, targetContext);
			return;
		}

		if (predicateRegistry.test(parameter)) {
			mappingStrategy.transform(objectMapper, sourceAccess, sourceContext, name, targetAccess, targetContext);
		}
	}

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			Object target, TypeDescriptor targetType, MappingContext targetContext,
			Mapping<? extends Field> targetMapping, Field targetField, MappingStrategy mappingStrategy)
			throws MappingException {
		for (Setter setter : targetField.getSetters()) {
			if (!predicateRegistry.test(setter)) {
				return;
			}
		}

		mappingStrategy.transform(objectMapper, sourceAccess, sourceContext, target, targetType, targetContext,
				targetMapping, targetField);
	}

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends Field> sourceMapping, Field sourceField,
			ObjectAccess targetAccess, MappingContext targetContext, MappingStrategy mappingStrategy)
			throws MappingException {
		for (Getter getter : sourceField.getGetters()) {
			if (!predicateRegistry.test(getter)) {
				return;
			}
		}

		mappingStrategy.transform(objectMapper, source, sourceType, sourceContext, sourceMapping, sourceField,
				targetAccess, targetContext);
	}

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends Field> sourceMapping, Object target,
			TypeDescriptor targetType, MappingContext targetContext, Mapping<? extends Field> targetMapping,
			Field targetField, MappingStrategy mappingStrategy) throws MappingException {
		for (Setter setter : targetField.getSetters()) {
			if (!predicateRegistry.test(setter)) {
				return;
			}
		}

		mappingStrategy.transform(objectMapper, source, sourceType, sourceContext, sourceMapping, target, targetType,
				targetContext, targetMapping, targetField);
	}

}

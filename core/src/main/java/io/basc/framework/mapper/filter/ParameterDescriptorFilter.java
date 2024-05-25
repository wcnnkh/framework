package io.basc.framework.mapper.filter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Getter;
import io.basc.framework.execution.Parameter;
import io.basc.framework.execution.Setter;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.access.ObjectAccess;
import io.basc.framework.mapper.entity.FieldDescriptor;
import io.basc.framework.mapper.entity.Mapping;
import io.basc.framework.mapper.entity.MappingContext;
import io.basc.framework.mapper.entity.MappingException;
import io.basc.framework.mapper.entity.MappingStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 通过参数描述来进行断言
 * 
 * @author wcnnkh
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ParameterDescriptorFilter extends ConfigurableParameterDescriptorMatcher implements MappingStrategyFilter {

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			String name, ObjectAccess targetAccess, MappingContext targetContext, MappingStrategy mappingStrategy)
			throws MappingException {
		Parameter parameter = sourceAccess.get(name);
		if (parameter == null) {
			mappingStrategy.transform(objectMapper, sourceAccess, sourceContext, name, targetAccess, targetContext);
			return;
		}

		if (test(sourceAccess.getTypeDescriptor(), parameter)) {
			mappingStrategy.transform(objectMapper, sourceAccess, sourceContext, name, targetAccess, targetContext);
		}
	}

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			Object target, TypeDescriptor targetType, MappingContext targetContext,
			Mapping<? extends FieldDescriptor> targetMapping, FieldDescriptor targetField,
			MappingStrategy mappingStrategy) throws MappingException {
		for (Setter setter : targetField.getAliasNames().map((e) -> targetField.setter().rename(e))) {
			if (!test(targetType, setter)) {
				return;
			}
		}

		mappingStrategy.transform(objectMapper, sourceAccess, sourceContext, target, targetType, targetContext,
				targetMapping, targetField);
	}

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends FieldDescriptor> sourceMapping, FieldDescriptor sourceField,
			ObjectAccess targetAccess, MappingContext targetContext, MappingStrategy mappingStrategy)
			throws MappingException {
		Getter getter = sourceField.getter().rename(sourceField.getName());
		if (!test(sourceType, getter)) {
			return;
		}

		mappingStrategy.transform(objectMapper, source, sourceType, sourceContext, sourceMapping, sourceField,
				targetAccess, targetContext);
	}

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends FieldDescriptor> sourceMapping, Object target,
			TypeDescriptor targetType, MappingContext targetContext, Mapping<? extends FieldDescriptor> targetMapping,
			FieldDescriptor targetField, MappingStrategy mappingStrategy) throws MappingException {
		for (Setter setter : targetField.getAliasNames().map((e) -> targetField.setter().rename(e))) {
			if (!test(targetType, setter)) {
				return;
			}
		}

		mappingStrategy.transform(objectMapper, source, sourceType, sourceContext, sourceMapping, target, targetType,
				targetContext, targetMapping, targetField);
	}
}

package io.basc.framework.mapper.filter;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingContext;
import io.basc.framework.mapper.MappingException;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.util.Services;
import io.basc.framework.value.Value;

/**
 * 将多个filter组合
 * 
 * @author wcnnkh
 *
 */
public class MappingStrategyFilters extends Services<MappingStrategyFilter> implements MappingStrategy {

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			String name, ObjectAccess targetAccess, MappingContext targetContext) throws MappingException {
		MappingStrategyChain chain = new MappingStrategyChain(iterator());
		chain.transform(objectMapper, sourceAccess, sourceContext, name, targetAccess, targetContext);
	}

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			Value target, MappingContext targetContext, Mapping<? extends Field> targetMapping, Field targetField)
			throws MappingException {
		MappingStrategyChain chain = new MappingStrategyChain(iterator());
		chain.transform(objectMapper, sourceAccess, sourceContext, target, targetContext, targetMapping, targetField);
	}

	@Override
	public void transform(ObjectMapper objectMapper, Value source, MappingContext sourceContext,
			Mapping<? extends Field> sourceMapping, Field sourceField, ObjectAccess targetAccess,
			MappingContext targetContext) throws MappingException {
		MappingStrategyChain chain = new MappingStrategyChain(iterator());
		chain.transform(objectMapper, source, sourceContext, sourceMapping, sourceField, targetAccess, targetContext);
	}

	@Override
	public void transform(ObjectMapper objectMapper, Value source, MappingContext sourceContext,
			Mapping<? extends Field> sourceMapping, Value target, MappingContext targetContext,
			Mapping<? extends Field> targetMapping, Field targetField) throws MappingException {
		MappingStrategyChain chain = new MappingStrategyChain(iterator());
		chain.transform(objectMapper, source, sourceContext, sourceMapping, target, targetContext, targetMapping,
				targetField);
	}

}

package io.basc.framework.mapper.filter;

import java.util.Iterator;

import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingContext;
import io.basc.framework.mapper.MappingException;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.util.Assert;
import io.basc.framework.value.Value;

public final class MappingStrategyChain implements MappingStrategy {
	private final Iterator<? extends MappingStrategyFilter> iterator;
	private final MappingStrategy nextStrategy;

	public MappingStrategyChain(Iterator<? extends MappingStrategyFilter> iterator) {
		this(iterator, null);
	}

	public MappingStrategyChain(Iterator<? extends MappingStrategyFilter> iterator,
			@Nullable MappingStrategy nextStrategy) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
		this.nextStrategy = nextStrategy;
	}

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			String name, ObjectAccess targetAccess, MappingContext targetContext) throws MappingException {
		if (iterator.hasNext()) {
			iterator.next().transform(objectMapper, sourceAccess, sourceContext, name, targetAccess, targetContext,
					this);
		} else if (nextStrategy != null) {
			nextStrategy.transform(objectMapper, sourceAccess, sourceContext, name, targetAccess, targetContext);
		}
	}

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			Value target, MappingContext targetContext, Mapping<? extends Field> targetMapping, Field targetField)
			throws MappingException {
		if (iterator.hasNext()) {
			iterator.next().transform(objectMapper, sourceAccess, sourceContext, target, targetContext, targetMapping,
					targetField, this);
		} else if (nextStrategy != null) {
			nextStrategy.transform(objectMapper, sourceAccess, sourceContext, target, targetContext, targetMapping,
					targetField);
		}
	}

	@Override
	public void transform(ObjectMapper objectMapper, Value source, MappingContext sourceContext,
			Mapping<? extends Field> sourceMapping, Field sourceField, ObjectAccess targetAccess,
			MappingContext targetContext) throws MappingException {
		if (iterator.hasNext()) {
			iterator.next().transform(objectMapper, source, sourceContext, sourceMapping, sourceField, targetAccess,
					targetContext, this);
		} else if (nextStrategy != null) {
			nextStrategy.transform(objectMapper, source, sourceContext, sourceMapping, sourceField, targetAccess,
					targetContext);
		}
	}

	@Override
	public void transform(ObjectMapper objectMapper, Value source, MappingContext sourceContext,
			Mapping<? extends Field> sourceMapping, Value target, MappingContext targetContext,
			Mapping<? extends Field> targetMapping, Field targetField) throws MappingException {
		if (iterator.hasNext()) {
			iterator.next().transform(objectMapper, source, sourceContext, sourceMapping, target, targetContext,
					targetMapping, targetField, this);
		} else if (nextStrategy != null) {
			nextStrategy.transform(objectMapper, source, sourceContext, sourceMapping, target, targetContext,
					targetMapping, targetField);
		}
	}
}

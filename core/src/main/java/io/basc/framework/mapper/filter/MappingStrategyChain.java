package io.basc.framework.mapper.filter;

import java.util.Iterator;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Member;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingContext;
import io.basc.framework.mapper.MappingException;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.util.Assert;

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
			Object target, TypeDescriptor targetType, MappingContext targetContext,
			Mapping<? extends Member> targetMapping, Member targetField) throws MappingException {
		if (iterator.hasNext()) {
			iterator.next().transform(objectMapper, sourceAccess, sourceContext, target, targetType, targetContext,
					targetMapping, targetField, this);
		} else if (nextStrategy != null) {
			nextStrategy.transform(objectMapper, sourceAccess, sourceContext, target, targetType, targetContext,
					targetMapping, targetField);
		}
	}

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends Member> sourceMapping, Member sourceField,
			ObjectAccess targetAccess, MappingContext targetContext) throws MappingException {
		if (iterator.hasNext()) {
			iterator.next().transform(objectMapper, source, sourceType, sourceContext, sourceMapping, sourceField,
					targetAccess, targetContext, this);
		} else if (nextStrategy != null) {
			nextStrategy.transform(objectMapper, source, sourceType, sourceContext, sourceMapping, sourceField,
					targetAccess, targetContext);
		}
	}

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends Member> sourceMapping, Object target,
			TypeDescriptor targetType, MappingContext targetContext, Mapping<? extends Member> targetMapping,
			Member targetField) throws MappingException {
		if (iterator.hasNext()) {
			iterator.next().transform(objectMapper, source, sourceType, sourceContext, sourceMapping, target,
					targetType, targetContext, targetMapping, targetField, this);
		} else if (nextStrategy != null) {
			nextStrategy.transform(objectMapper, source, sourceType, sourceContext, sourceMapping, target, targetType,
					targetContext, targetMapping, targetField);
		}
	}
}

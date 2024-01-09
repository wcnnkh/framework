package io.basc.framework.mapper.filter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Item;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingContext;
import io.basc.framework.mapper.MappingException;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.util.Assert;

public class FilterableMappingStrategy implements MappingStrategy {
	private final Iterable<? extends MappingStrategyFilter> filters;
	private final MappingStrategy dottomlessMappingStrategy;

	public FilterableMappingStrategy(Iterable<? extends MappingStrategyFilter> filters) {
		this(filters, null);
	}

	public FilterableMappingStrategy(Iterable<? extends MappingStrategyFilter> filters,
			@Nullable MappingStrategy dottomlessMappingStrategy) {
		Assert.requiredArgument(filters != null, "filters");
		this.filters = filters;
		this.dottomlessMappingStrategy = dottomlessMappingStrategy;
	}

	public Iterable<? extends MappingStrategyFilter> getFilters() {
		return filters;
	}

	public MappingStrategy getDottomlessMappingStrategy() {
		return dottomlessMappingStrategy;
	}

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			String name, ObjectAccess targetAccess, MappingContext targetContext) throws MappingException {
		MappingStrategyChain chain = new MappingStrategyChain(this.filters.iterator(), this.dottomlessMappingStrategy);
		chain.transform(objectMapper, sourceAccess, sourceContext, name, targetAccess, targetContext);
	}

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			Object target, TypeDescriptor targetType, MappingContext targetContext,
			Mapping<? extends Item> targetMapping, Item targetField) throws MappingException {
		MappingStrategyChain chain = new MappingStrategyChain(this.filters.iterator(), this.dottomlessMappingStrategy);
		chain.transform(objectMapper, sourceAccess, sourceContext, target, targetType, targetContext, targetMapping,
				targetField);
	}

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends Item> sourceMapping, Item sourceField,
			ObjectAccess targetAccess, MappingContext targetContext) throws MappingException {
		MappingStrategyChain chain = new MappingStrategyChain(this.filters.iterator(), this.dottomlessMappingStrategy);
		chain.transform(objectMapper, source, sourceType, sourceContext, sourceMapping, sourceField, targetAccess,
				targetContext);
	}

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends Item> sourceMapping, Object target,
			TypeDescriptor targetType, MappingContext targetContext, Mapping<? extends Item> targetMapping,
			Item targetField) throws MappingException {
		MappingStrategyChain chain = new MappingStrategyChain(this.filters.iterator(), this.dottomlessMappingStrategy);
		chain.transform(objectMapper, source, sourceType, sourceContext, sourceMapping, target, targetType,
				targetContext, targetMapping, targetField);
	}

}

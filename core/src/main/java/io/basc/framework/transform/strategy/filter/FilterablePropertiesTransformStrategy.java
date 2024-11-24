package io.basc.framework.transform.strategy.filter;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.transform.strategy.PropertiesTransformContext;
import io.basc.framework.transform.strategy.PropertiesTransformStrategy;
import io.basc.framework.util.Assert;

public class FilterablePropertiesTransformStrategy implements PropertiesTransformStrategy {
	private final Iterable<? extends PropertiesTransformFilter> filters;
	private final PropertiesTransformStrategy dottomlessStrategy;

	public FilterablePropertiesTransformStrategy(Iterable<? extends PropertiesTransformFilter> filters) {
		this(filters, null);
	}

	public FilterablePropertiesTransformStrategy(Iterable<? extends PropertiesTransformFilter> filters,
			@Nullable PropertiesTransformStrategy dottomlessStrategy) {
		Assert.requiredArgument(filters != null, "filters");
		this.filters = filters;
		this.dottomlessStrategy = dottomlessStrategy;
	}

	public Iterable<? extends PropertiesTransformFilter> getFilters() {
		return filters;
	}

	public PropertiesTransformStrategy getDottomlessStrategy() {
		return dottomlessStrategy;
	}

	@Override
	public void doTransform(PropertiesTransformContext sourceContext, Properties sourceProperties,
			TypeDescriptor sourceTypeDescriptor, Property sourceProperty, PropertiesTransformContext targetContext,
			Properties targetProperties, TypeDescriptor targetTypeDescriptor) {
		PropertiesTransformStrategyChain chain = new PropertiesTransformStrategyChain(this.filters.iterator(),
				this.dottomlessStrategy);
		chain.doTransform(sourceContext, sourceProperties, sourceTypeDescriptor, sourceProperty, targetContext,
				targetProperties, targetTypeDescriptor);
	}
}

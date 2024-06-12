package io.basc.framework.transform.strategy.filter;

import java.util.Iterator;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.transform.strategy.PropertiesTransformContext;
import io.basc.framework.transform.strategy.PropertiesTransformStrategy;
import io.basc.framework.util.Assert;

public final class PropertiesTransformStrategyChain implements PropertiesTransformStrategy {
	private final Iterator<? extends PropertiesTransformFilter> iterator;
	private final PropertiesTransformStrategy nextStrategy;

	public PropertiesTransformStrategyChain(Iterator<? extends PropertiesTransformFilter> iterator) {
		this(iterator, null);
	}

	public PropertiesTransformStrategyChain(Iterator<? extends PropertiesTransformFilter> iterator,
			@Nullable PropertiesTransformStrategy nextStrategy) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
		this.nextStrategy = nextStrategy;
	}

	@Override
	public void doTransform(PropertiesTransformContext sourceContext, Properties sourceProperties,
			TypeDescriptor sourceTypeDescriptor, Property sourceProperty, PropertiesTransformContext targetContext,
			Properties targetProperties, TypeDescriptor targetTypeDescriptor) {
		if (iterator.hasNext()) {
			iterator.next().doFilter(sourceContext, sourceProperties, sourceTypeDescriptor, sourceProperty,
					targetContext, targetProperties, targetTypeDescriptor, nextStrategy);
		} else if (nextStrategy != null) {
			nextStrategy.doTransform(sourceContext, sourceProperties, sourceTypeDescriptor, sourceProperty,
					targetContext, targetProperties, targetTypeDescriptor);
		}
	}
}

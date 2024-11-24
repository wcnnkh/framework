package io.basc.framework.transform.strategy.filter;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.transform.strategy.PropertiesTransformContext;
import io.basc.framework.transform.strategy.PropertiesTransformStrategy;

/**
 * 映射策略拦截器
 * 
 * @see PropertiesTransformStrategyChain
 * @author wcnnkh
 *
 */
public interface PropertiesTransformFilter {
	void doFilter(@Nullable PropertiesTransformContext sourceContext, Properties sourceProperties,
			TypeDescriptor sourceTypeDescriptor, Property sourceProperty,
			@Nullable PropertiesTransformContext targetContext, Properties targetProperties,
			TypeDescriptor targetTypeDescriptor, PropertiesTransformStrategy strategy);
}

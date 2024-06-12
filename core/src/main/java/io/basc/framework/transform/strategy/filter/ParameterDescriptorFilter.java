package io.basc.framework.transform.strategy.filter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.ConfigurableParameterDescriptorPredicate;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.transform.strategy.PropertiesTransformContext;
import io.basc.framework.transform.strategy.PropertiesTransformStrategy;
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
public class ParameterDescriptorFilter extends ConfigurableParameterDescriptorPredicate
		implements PropertiesTransformFilter {

	@Override
	public void doFilter(PropertiesTransformContext sourceContext, Properties sourceProperties,
			TypeDescriptor sourceTypeDescriptor, Property sourceProperty, PropertiesTransformContext targetContext,
			Properties targetProperties, TypeDescriptor targetTypeDescriptor, PropertiesTransformStrategy strategy) {
		if (test(sourceTypeDescriptor, sourceProperty)) {
			strategy.doTransform(sourceContext, sourceProperties, sourceTypeDescriptor, sourceProperty, targetContext,
					targetProperties, targetTypeDescriptor);
		}
	}
}

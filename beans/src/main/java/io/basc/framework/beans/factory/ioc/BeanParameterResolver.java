package io.basc.framework.beans.factory.ioc;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.core.execution.ParameterDescriptorTemplate;
import io.basc.framework.core.execution.Parameters;

/**
 * 依赖注入参数解析
 * 
 * @author shuchaowen
 *
 */
public interface BeanParameterResolver {
	boolean canResolveParameters(BeanFactory beanFactory, ParameterDescriptorTemplate template);

	Parameters resolveParameters(BeanFactory beanFactory, ParameterDescriptorTemplate template);
}

package run.soeasy.framework.beans.factory.ioc;

import run.soeasy.framework.beans.factory.BeanFactory;
import run.soeasy.framework.core.execution.ParameterDescriptorTemplate;
import run.soeasy.framework.core.execution.Parameters;

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

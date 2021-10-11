package io.basc.framework.beans.ioc.value;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.annotation.Value;
import io.basc.framework.io.Resource;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Field;

/**
 * 智能注入
 * 
 * @see ResourceValueProcessor
 * @see EnvironmentValueProceor
 * @author shuchaowen
 *
 */
public class SmartValueProcessor implements ValueProcessor {
	private static final ResourceValueProcessor RESOURCE_VALUE_PROCESSER = new ResourceValueProcessor();
	private static final EnvironmentValueProceor ENVIRONMENT_VALUE_PROCESS = new EnvironmentValueProceor();

	private static Logger logger = LoggerFactory.getLogger(SmartValueProcessor.class);
	
	@Override
	public void process(BeanDefinition beanDefinition, BeanFactory beanFactory, Object bean, Field field, Value value) {
		String source = value.value();
		if (EnvironmentValueProceor.isEnvironment(source)) {
			ENVIRONMENT_VALUE_PROCESS.process(beanDefinition, beanFactory, bean, field, value);
			return;
		}

		Resource resource = beanFactory.getEnvironment().getResource(source);
		if (resource != null && resource.exists()) {
			if(logger.isDebugEnabled()) {
				logger.debug("Process resource value field: {}", field);
			}
			RESOURCE_VALUE_PROCESSER.process(beanDefinition, beanFactory, bean, field, value);
			return;
		}

		ENVIRONMENT_VALUE_PROCESS.process(beanDefinition, beanFactory, bean, field, value);
	}

}

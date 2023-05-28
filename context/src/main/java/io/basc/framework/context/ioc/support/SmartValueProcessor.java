package io.basc.framework.context.ioc.support;

import io.basc.framework.beans.config.BeanDefinition;
import io.basc.framework.context.Context;
import io.basc.framework.context.ioc.ValueDefinition;
import io.basc.framework.context.ioc.ValueProcessor;
import io.basc.framework.io.Resource;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Field;

/**
 * 智能注入
 * 
 * @see ResourceValueProcessor
 * @see EnvironmentValueProceor
 * @author wcnnkh
 *
 */
public class SmartValueProcessor implements ValueProcessor {
	private static final ResourceValueProcessor RESOURCE_VALUE_PROCESSER = new ResourceValueProcessor();
	private static final EnvironmentValueProceor ENVIRONMENT_VALUE_PROCESS = new EnvironmentValueProceor();

	private static Logger logger = LoggerFactory.getLogger(SmartValueProcessor.class);

	@Override
	public void process(Context context, Object bean, BeanDefinition beanDefinition, Field field,
			ValueDefinition valueDefinition) {
		for (String source : valueDefinition.getNames()) {
			if (EnvironmentValueProceor.isEnvironment(source)) {
				ENVIRONMENT_VALUE_PROCESS.process(context, bean, beanDefinition, field, valueDefinition);
				return;
			}

			Resource resource = context.getResourceLoader().getResource(source);
			if (resource != null && resource.exists()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Process resource value field: {}", field);
				}
				RESOURCE_VALUE_PROCESSER.process(context, bean, beanDefinition, field, valueDefinition);
				return;
			}

			ENVIRONMENT_VALUE_PROCESS.process(context, bean, beanDefinition, field, valueDefinition);
		}
	}

}

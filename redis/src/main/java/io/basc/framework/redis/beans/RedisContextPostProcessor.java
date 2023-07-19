package io.basc.framework.redis.beans;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.config.ContextPostProcessor;
import io.basc.framework.core.Ordered;
import io.basc.framework.redis.RedisConfiguration;

@ConditionalOnParameters(order = Ordered.HIGHEST_PRECEDENCE)
public class RedisContextPostProcessor implements ContextPostProcessor {
	
	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		if (!context.containsDefinition(RedisConfiguration.class.getName())) {
			context.registerDefinition(new RedisConfigBeanDefinition(context));
		}
	}

}

package io.basc.framework.netflix.eureka.beans;

import com.netflix.discovery.EurekaClientConfig;

import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.ContextPostProcessor;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.netflix.eureka.EurekaClientConfigBean;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class EureakContextPostProcessor implements ContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		EurekaClientDefinition eurekaClientDefinition = new EurekaClientDefinition(context);
		if (!context.containsDefinition(eurekaClientDefinition.getId())) {
			context.registerDefinition(eurekaClientDefinition);
		}

		ApplicationInfoManagerDefinition applicationInfoManagerDefinition = new ApplicationInfoManagerDefinition(
				context);
		if (!context.containsDefinition(applicationInfoManagerDefinition.getId())) {
			context.registerDefinition(applicationInfoManagerDefinition);
		}

		if (!context.isAlias(EurekaClientConfig.class.getName())) {
			context.registerAlias(EurekaClientConfigBean.class.getName(), EurekaClientConfig.class.getName());
		}

		EurekaInstanceConfigBeanDefinition eurekaInstanceConfigDefinition = new EurekaInstanceConfigBeanDefinition(
				context);
		if (!context.containsDefinition(eurekaInstanceConfigDefinition.getId())) {
			context.registerDefinition(eurekaInstanceConfigDefinition);
		}
	}
}

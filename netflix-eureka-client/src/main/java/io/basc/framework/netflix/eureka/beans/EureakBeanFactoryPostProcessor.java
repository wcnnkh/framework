package io.basc.framework.netflix.eureka.beans;

import com.netflix.discovery.EurekaClientConfig;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.env.EnvironmentPostProcessor;
import io.basc.framework.netflix.eureka.EurekaClientConfigBean;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class EureakBeanFactoryPostProcessor implements EnvironmentPostProcessor {

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment) {
		EurekaClientDefinition eurekaClientDefinition = new EurekaClientDefinition(environment);
		if (!environment.containsDefinition(eurekaClientDefinition.getId())) {
			environment.registerDefinition(eurekaClientDefinition);
		}

		ApplicationInfoManagerDefinition applicationInfoManagerDefinition = new ApplicationInfoManagerDefinition(
				environment);
		if (!environment.containsDefinition(applicationInfoManagerDefinition.getId())) {
			environment.registerDefinition(applicationInfoManagerDefinition);
		}

		if (!environment.isAlias(EurekaClientConfig.class.getName())) {
			environment.registerAlias(EurekaClientConfigBean.class.getName(), EurekaClientConfig.class.getName());
		}

		EurekaInstanceConfigBeanDefinition eurekaInstanceConfigDefinition = new EurekaInstanceConfigBeanDefinition(
				environment);
		if (!environment.containsDefinition(eurekaInstanceConfigDefinition.getId())) {
			environment.registerDefinition(eurekaInstanceConfigDefinition);
		}
	}
}

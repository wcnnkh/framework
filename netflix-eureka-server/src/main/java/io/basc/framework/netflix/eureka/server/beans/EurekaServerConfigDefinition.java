package io.basc.framework.netflix.eureka.server.beans;

import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.EurekaServerConfig;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.netflix.eureka.server.EurekaServerConfigBean;

public class EurekaServerConfigDefinition extends DefaultBeanDefinition {

	public EurekaServerConfigDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, EurekaServerConfig.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(EurekaClientConfig.class);
	}

	@Override
	public Object create() throws BeansException {
		EurekaClientConfig clientConfig = beanFactory.getInstance(EurekaClientConfig.class);
		EurekaServerConfigBean server = new EurekaServerConfigBean();
		if (clientConfig.shouldRegisterWithEureka()) {
			// Set a sensible default if we are supposed to replicate
			server.setRegistrySyncRetries(5);
		}
		return server;
	}
}
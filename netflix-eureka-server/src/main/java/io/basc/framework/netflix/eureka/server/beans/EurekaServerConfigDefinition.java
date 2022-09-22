package io.basc.framework.netflix.eureka.server.beans;

import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.EurekaServerConfig;

import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.BeansException;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.netflix.eureka.server.EurekaServerConfigBean;

public class EurekaServerConfigDefinition extends FactoryBeanDefinition {

	public EurekaServerConfigDefinition(BeanFactory beanFactory) {
		super(beanFactory, EurekaServerConfig.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(EurekaClientConfig.class);
	}

	@Override
	public Object create() throws BeansException {
		EurekaClientConfig clientConfig = getBeanFactory().getInstance(EurekaClientConfig.class);
		EurekaServerConfigBean server = new EurekaServerConfigBean();
		if (clientConfig.shouldRegisterWithEureka()) {
			// Set a sensible default if we are supposed to replicate
			server.setRegistrySyncRetries(5);
		}
		return server;
	}
}
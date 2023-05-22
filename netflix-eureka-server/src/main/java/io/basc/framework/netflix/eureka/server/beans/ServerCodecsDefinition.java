package io.basc.framework.netflix.eureka.server.beans;

import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.resources.ServerCodecs;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeansException;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.netflix.eureka.server.CloudServerCodecs;

public class ServerCodecsDefinition extends FactoryBeanDefinition {

	public ServerCodecsDefinition(BeanFactory beanFactory) {
		super(beanFactory, ServerCodecs.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(EurekaServerConfig.class);
	}

	@Override
	public Object create() throws BeansException {
		return new CloudServerCodecs(getBeanFactory().getInstance(EurekaServerConfig.class));
	}
}
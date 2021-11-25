package io.basc.framework.netflix.eureka.server.beans;

import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.resources.ServerCodecs;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.netflix.eureka.server.CloudServerCodecs;

public class ServerCodecsDefinition extends DefaultBeanDefinition {

	public ServerCodecsDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, ServerCodecs.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(EurekaServerConfig.class);
	}

	@Override
	public Object create() throws BeansException {
		return new CloudServerCodecs(beanFactory.getInstance(EurekaServerConfig.class));
	}
}
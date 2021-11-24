package io.basc.framework.netflix.eureka.server.beans;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.netflix.eureka.server.InstanceRegistry;
import io.basc.framework.netflix.eureka.server.InstanceRegistryProperties;

public class PeerAwareInstanceRegistryDefinition extends DefaultBeanDefinition {

	public PeerAwareInstanceRegistryDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, PeerAwareInstanceRegistry.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(ServerCodecs.class) && beanFactory.isInstance(EurekaServerConfig.class)
				&& beanFactory.isInstance(EurekaClientConfig.class) && beanFactory.isInstance(EurekaClient.class)
				&& beanFactory.isInstance(InstanceRegistryProperties.class);
	}

	@Override
	public Object create() throws BeansException {
		ServerCodecs serverCodecs = beanFactory.getInstance(ServerCodecs.class);
		EurekaServerConfig serverConfig = beanFactory.getInstance(EurekaServerConfig.class);
		EurekaClientConfig clientConfig = beanFactory.getInstance(EurekaClientConfig.class);
		EurekaClient eurekaClient = beanFactory.getInstance(EurekaClient.class);
		eurekaClient.getApplications(); // force initialization
		InstanceRegistryProperties instanceRegistryProperties = beanFactory
				.getInstance(InstanceRegistryProperties.class);
		return new InstanceRegistry(serverConfig, clientConfig, serverCodecs, eurekaClient,
				instanceRegistryProperties.getExpectedNumberOfClientsSendingRenews(),
				instanceRegistryProperties.getDefaultOpenForTrafficCount());
	}
}
package io.basc.framework.netflix.eureka.server.beans;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;

import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.BeansException;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.netflix.eureka.server.InstanceRegistry;
import io.basc.framework.netflix.eureka.server.InstanceRegistryProperties;

public class PeerAwareInstanceRegistryDefinition extends FactoryBeanDefinition {

	public PeerAwareInstanceRegistryDefinition(BeanFactory beanFactory) {
		super(beanFactory, PeerAwareInstanceRegistry.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(ServerCodecs.class) && getBeanFactory().isInstance(EurekaServerConfig.class)
				&& getBeanFactory().isInstance(EurekaClientConfig.class)
				&& getBeanFactory().isInstance(EurekaClient.class)
				&& getBeanFactory().isInstance(InstanceRegistryProperties.class);
	}

	@Override
	public Object create() throws BeansException {
		ServerCodecs serverCodecs = getBeanFactory().getInstance(ServerCodecs.class);
		EurekaServerConfig serverConfig = getBeanFactory().getInstance(EurekaServerConfig.class);
		EurekaClientConfig clientConfig = getBeanFactory().getInstance(EurekaClientConfig.class);
		EurekaClient eurekaClient = getBeanFactory().getInstance(EurekaClient.class);
		eurekaClient.getApplications(); // force initialization
		InstanceRegistryProperties instanceRegistryProperties = getBeanFactory()
				.getInstance(InstanceRegistryProperties.class);
		return new InstanceRegistry(serverConfig, clientConfig, serverCodecs, eurekaClient,
				instanceRegistryProperties.getExpectedNumberOfClientsSendingRenews(),
				instanceRegistryProperties.getDefaultOpenForTrafficCount());
	}
}
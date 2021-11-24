package io.basc.framework.netflix.eureka.server.beans;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class EurekaServerBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		EurekaServerConfigDefinition eurekaServerConfigDefinition = new EurekaServerConfigDefinition(beanFactory);
		if (!beanFactory.containsDefinition(eurekaServerConfigDefinition.getId())) {
			beanFactory.registerDefinition(eurekaServerConfigDefinition);
		}

		EurekaServerContextDefinition eurekaServerContextDefinition = new EurekaServerContextDefinition(beanFactory);
		if (!beanFactory.containsDefinition(eurekaServerContextDefinition.getId())) {
			beanFactory.registerDefinition(eurekaServerContextDefinition);
		}

		PeerAwareInstanceRegistryDefinition peerAwareInstanceRegistryDefinition = new PeerAwareInstanceRegistryDefinition(
				beanFactory);
		if (!beanFactory.containsDefinition(peerAwareInstanceRegistryDefinition.getId())) {
			beanFactory.registerDefinition(peerAwareInstanceRegistryDefinition);
		}

		PeerEurekaNodesDefinition peerEurekaNodesDefinition = new PeerEurekaNodesDefinition(beanFactory);
		if (!beanFactory.containsDefinition(peerEurekaNodesDefinition.getId())) {
			beanFactory.registerDefinition(peerEurekaNodesDefinition);
		}

		ServerCodecsDefinition serverCodecsDefinition = new ServerCodecsDefinition(beanFactory);
		if (!beanFactory.containsDefinition(serverCodecsDefinition.getId())) {
			beanFactory.registerDefinition(serverCodecsDefinition);
		}
	}

}

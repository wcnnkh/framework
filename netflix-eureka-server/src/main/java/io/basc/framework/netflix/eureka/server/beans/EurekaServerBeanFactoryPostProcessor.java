package io.basc.framework.netflix.eureka.server.beans;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.env.EnvironmentPostProcessor;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class EurekaServerBeanFactoryPostProcessor implements EnvironmentPostProcessor {

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment) {
		EurekaServerConfigDefinition eurekaServerConfigDefinition = new EurekaServerConfigDefinition(environment);
		if (!environment.containsDefinition(eurekaServerConfigDefinition.getId())) {
			environment.registerDefinition(eurekaServerConfigDefinition);
		}

		EurekaServerContextDefinition eurekaServerContextDefinition = new EurekaServerContextDefinition(environment);
		if (!environment.containsDefinition(eurekaServerContextDefinition.getId())) {
			environment.registerDefinition(eurekaServerContextDefinition);
		}

		PeerAwareInstanceRegistryDefinition peerAwareInstanceRegistryDefinition = new PeerAwareInstanceRegistryDefinition(
				environment);
		if (!environment.containsDefinition(peerAwareInstanceRegistryDefinition.getId())) {
			environment.registerDefinition(peerAwareInstanceRegistryDefinition);
		}

		PeerEurekaNodesDefinition peerEurekaNodesDefinition = new PeerEurekaNodesDefinition(environment);
		if (!environment.containsDefinition(peerEurekaNodesDefinition.getId())) {
			environment.registerDefinition(peerEurekaNodesDefinition);
		}

		ServerCodecsDefinition serverCodecsDefinition = new ServerCodecsDefinition(environment);
		if (!environment.containsDefinition(serverCodecsDefinition.getId())) {
			environment.registerDefinition(serverCodecsDefinition);
		}
	}
}

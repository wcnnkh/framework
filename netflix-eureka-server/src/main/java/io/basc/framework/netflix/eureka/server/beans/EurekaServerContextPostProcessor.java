package io.basc.framework.netflix.eureka.server.beans;

import com.netflix.eureka.EurekaServerConfig;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.config.ContextPostProcessor;
import io.basc.framework.core.Ordered;

@ConditionalOnParameters(order = Ordered.LOWEST_PRECEDENCE)
public class EurekaServerContextPostProcessor implements ContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		EurekaServerConfigDefinition eurekaServerConfigDefinition = new EurekaServerConfigDefinition(context);
		if (!context.containsDefinition(EurekaServerConfig.class.getName())) {
			context.registerDefinition(EurekaServerConfig.class.getName(), eurekaServerConfigDefinition);
		}

		EurekaServerContextDefinition eurekaServerContextDefinition = new EurekaServerContextDefinition(context);
		if (!context.containsDefinition(eurekaServerContextDefinition.getId())) {
			context.registerDefinition(eurekaServerContextDefinition);
		}

		PeerAwareInstanceRegistryDefinition peerAwareInstanceRegistryDefinition = new PeerAwareInstanceRegistryDefinition(
				context);
		if (!context.containsDefinition(peerAwareInstanceRegistryDefinition.getId())) {
			context.registerDefinition(peerAwareInstanceRegistryDefinition);
		}

		PeerEurekaNodesDefinition peerEurekaNodesDefinition = new PeerEurekaNodesDefinition(context);
		if (!context.containsDefinition(peerEurekaNodesDefinition.getId())) {
			context.registerDefinition(peerEurekaNodesDefinition);
		}

		ServerCodecsDefinition serverCodecsDefinition = new ServerCodecsDefinition(context);
		if (!context.containsDefinition(serverCodecsDefinition.getId())) {
			context.registerDefinition(serverCodecsDefinition);
		}
	}
}

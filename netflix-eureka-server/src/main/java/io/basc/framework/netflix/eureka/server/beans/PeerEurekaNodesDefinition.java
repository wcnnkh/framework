package io.basc.framework.netflix.eureka.server.beans;

import java.util.Collections;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.cluster.PeerEurekaNodes;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.core.env.Environment;
import io.basc.framework.core.env.EnvironmentBeanDefinition;
import io.basc.framework.netflix.eureka.server.RefreshablePeerEurekaNodes;
import io.basc.framework.netflix.eureka.server.ReplicationClientAdditionalFilters;

public class PeerEurekaNodesDefinition extends EnvironmentBeanDefinition {

	public PeerEurekaNodesDefinition(Environment environment) {
		super(environment, PeerEurekaNodes.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(ServerCodecs.class) && getBeanFactory().isInstance(EurekaServerConfig.class)
				&& getBeanFactory().isInstance(EurekaClientConfig.class)
				&& getBeanFactory().isInstance(ApplicationInfoManager.class)
				&& getBeanFactory().isInstance(PeerAwareInstanceRegistry.class);
	}

	@Override
	public Object create() throws BeansException {
		PeerAwareInstanceRegistry registry = getBeanFactory().getInstance(PeerAwareInstanceRegistry.class);
		EurekaServerConfig serverConfig = getBeanFactory().getInstance(EurekaServerConfig.class);
		EurekaClientConfig clientConfig = getBeanFactory().getInstance(EurekaClientConfig.class);
		ServerCodecs serverCodecs = getBeanFactory().getInstance(ServerCodecs.class);
		ApplicationInfoManager applicationInfoManager = getBeanFactory().getInstance(ApplicationInfoManager.class);
		ReplicationClientAdditionalFilters replicationClientAdditionalFilters = getReplicationClientAdditionalFilters(
				getBeanFactory());
		return new RefreshablePeerEurekaNodes(registry, serverConfig, clientConfig, serverCodecs,
				applicationInfoManager, replicationClientAdditionalFilters, getEnvironment());
	}

	private static ReplicationClientAdditionalFilters getReplicationClientAdditionalFilters(BeanFactory beanFactory) {
		if (!beanFactory.isInstance(ReplicationClientAdditionalFilters.class)) {
			return new ReplicationClientAdditionalFilters(Collections.emptySet());
		}
		return beanFactory.getInstance(ReplicationClientAdditionalFilters.class);
	}
}
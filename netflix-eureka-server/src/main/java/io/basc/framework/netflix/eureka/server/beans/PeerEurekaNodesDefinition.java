package io.basc.framework.netflix.eureka.server.beans;

import java.util.Collections;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.cluster.PeerEurekaNodes;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.netflix.eureka.server.RefreshablePeerEurekaNodes;
import io.basc.framework.netflix.eureka.server.ReplicationClientAdditionalFilters;

public class PeerEurekaNodesDefinition extends DefaultBeanDefinition {

	public PeerEurekaNodesDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, PeerEurekaNodes.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(ServerCodecs.class) && beanFactory.isInstance(EurekaServerConfig.class)
				&& beanFactory.isInstance(EurekaClientConfig.class)
				&& beanFactory.isInstance(ApplicationInfoManager.class)
				&& beanFactory.isInstance(PeerAwareInstanceRegistry.class);
	}

	@Override
	public Object create() throws BeansException {
		PeerAwareInstanceRegistry registry = beanFactory.getInstance(PeerAwareInstanceRegistry.class);
		EurekaServerConfig serverConfig = beanFactory.getInstance(EurekaServerConfig.class);
		EurekaClientConfig clientConfig = beanFactory.getInstance(EurekaClientConfig.class);
		ServerCodecs serverCodecs = beanFactory.getInstance(ServerCodecs.class);
		ApplicationInfoManager applicationInfoManager = beanFactory.getInstance(ApplicationInfoManager.class);
		ReplicationClientAdditionalFilters replicationClientAdditionalFilters = getReplicationClientAdditionalFilters(
				beanFactory);
		return new RefreshablePeerEurekaNodes(registry, serverConfig, clientConfig, serverCodecs,
				applicationInfoManager, replicationClientAdditionalFilters, beanFactory.getEnvironment());
	}

	private static ReplicationClientAdditionalFilters getReplicationClientAdditionalFilters(BeanFactory beanFactory) {
		if (!beanFactory.isInstance(ReplicationClientAdditionalFilters.class)) {
			return new ReplicationClientAdditionalFilters(Collections.emptySet());
		}
		return beanFactory.getInstance(ReplicationClientAdditionalFilters.class);
	}
}
package io.basc.framework.netflix.eureka.server.beans;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.eureka.DefaultEurekaServerContext;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.cluster.PeerEurekaNodes;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;

public class EurekaServerContextDefinition extends DefaultBeanDefinition {

	public EurekaServerContextDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, EurekaServerContext.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(EurekaServerConfig.class) && beanFactory.isInstance(ServerCodecs.class)
				&& beanFactory.isInstance(PeerAwareInstanceRegistry.class)
				&& beanFactory.isInstance(ApplicationInfoManager.class)
				&& beanFactory.isInstance(PeerEurekaNodes.class);
	}

	@Override
	public Object create() throws BeansException {
		EurekaServerConfig eurekaServerConfig = beanFactory.getInstance(EurekaServerConfig.class);
		ServerCodecs serverCodecs = beanFactory.getInstance(ServerCodecs.class);
		PeerAwareInstanceRegistry registry = beanFactory.getInstance(PeerAwareInstanceRegistry.class);
		PeerEurekaNodes peerEurekaNodes = beanFactory.getInstance(PeerEurekaNodes.class);
		ApplicationInfoManager applicationInfoManager = beanFactory.getInstance(ApplicationInfoManager.class);
		return new DefaultEurekaServerContext(eurekaServerConfig, serverCodecs, registry, peerEurekaNodes,
				applicationInfoManager);
	}
}
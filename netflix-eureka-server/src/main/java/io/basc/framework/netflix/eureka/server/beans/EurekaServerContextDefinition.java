package io.basc.framework.netflix.eureka.server.beans;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.eureka.DefaultEurekaServerContext;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.cluster.PeerEurekaNodes;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeansException;
import io.basc.framework.factory.support.FactoryBeanDefinition;

public class EurekaServerContextDefinition extends FactoryBeanDefinition {

	public EurekaServerContextDefinition(BeanFactory beanFactory) {
		super(beanFactory, EurekaServerContext.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(EurekaServerConfig.class) && getBeanFactory().isInstance(ServerCodecs.class)
				&& getBeanFactory().isInstance(PeerAwareInstanceRegistry.class)
				&& getBeanFactory().isInstance(ApplicationInfoManager.class)
				&& getBeanFactory().isInstance(PeerEurekaNodes.class);
	}

	@Override
	public Object create() throws BeansException {
		EurekaServerConfig eurekaServerConfig = getBeanFactory().getInstance(EurekaServerConfig.class);
		ServerCodecs serverCodecs = getBeanFactory().getInstance(ServerCodecs.class);
		PeerAwareInstanceRegistry registry = getBeanFactory().getInstance(PeerAwareInstanceRegistry.class);
		PeerEurekaNodes peerEurekaNodes = getBeanFactory().getInstance(PeerEurekaNodes.class);
		ApplicationInfoManager applicationInfoManager = getBeanFactory().getInstance(ApplicationInfoManager.class);
		return new DefaultEurekaServerContext(eurekaServerConfig, serverCodecs, registry, peerEurekaNodes,
				applicationInfoManager);
	}
}
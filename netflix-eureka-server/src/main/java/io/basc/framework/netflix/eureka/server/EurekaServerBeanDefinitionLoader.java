package io.basc.framework.netflix.eureka.server;

import java.util.Collections;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.DefaultEurekaServerContext;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.cluster.PeerEurekaNodes;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeansException;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentBeanDefinition;
import io.basc.framework.factory.support.BeanDefinitionLoader;
import io.basc.framework.factory.support.BeanDefinitionLoaderChain;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.util.ClassUtils;

@Provider
public class EurekaServerBeanDefinitionLoader implements BeanDefinitionLoader {

	@Override
	public BeanDefinition load(BeanFactory beanFactory, ClassLoader classLoader, String name,
			BeanDefinitionLoaderChain loaderChain) {
		Class<?> sourceClass = ClassUtils.getClass(name, classLoader);
		if (sourceClass == null) {
			return null;
		}

		if (sourceClass == ServerCodecs.class) {
			return new ServerCodecsBuilder(beanFactory, sourceClass);
		}

		if (sourceClass == PeerAwareInstanceRegistry.class) {
			return new PeerAwareInstanceRegistryBuilder(beanFactory, sourceClass);
		}

		if (sourceClass == PeerEurekaNodes.class && beanFactory.isInstance(Environment.class)) {
			return new PeerEurekaNodesBuilder(beanFactory.getInstance(Environment.class), sourceClass);
		}

		if (sourceClass == EurekaServerContext.class) {
			return new EurekaServerContextBuilder(beanFactory, sourceClass);
		}

		if (sourceClass == EurekaServerConfig.class) {
			return new EurekaServerConfigBuilder(beanFactory, sourceClass);
		}

		return loaderChain.load(beanFactory, classLoader, name);
	}

	private static class EurekaServerConfigBuilder extends FactoryBeanDefinition {

		public EurekaServerConfigBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		@Override
		public boolean isInstance() {
			return getBeanFactory().isInstance(EurekaClientConfig.class);
		}

		@Override
		public Object create() throws BeansException {
			EurekaClientConfig clientConfig = getBeanFactory().getInstance(EurekaClientConfig.class);
			EurekaServerConfigBean server = new EurekaServerConfigBean();
			if (clientConfig.shouldRegisterWithEureka()) {
				// Set a sensible default if we are supposed to replicate
				server.setRegistrySyncRetries(5);
			}
			return server;
		}
	}

	private static class EurekaServerContextBuilder extends FactoryBeanDefinition {

		public EurekaServerContextBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		@Override
		public boolean isInstance() {
			return getBeanFactory().isInstance(EurekaServerConfig.class)
					&& getBeanFactory().isInstance(ServerCodecs.class)
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

	private static ReplicationClientAdditionalFilters getReplicationClientAdditionalFilters(BeanFactory beanFactory) {
		if (!beanFactory.isInstance(ReplicationClientAdditionalFilters.class)) {
			return new ReplicationClientAdditionalFilters(Collections.emptySet());
		}
		return beanFactory.getInstance(ReplicationClientAdditionalFilters.class);
	}

	private static class PeerEurekaNodesBuilder extends EnvironmentBeanDefinition {

		public PeerEurekaNodesBuilder(Environment environment, Class<?> sourceClass) {
			super(environment, sourceClass);
		}

		@Override
		public boolean isInstance() {
			return getBeanFactory().isInstance(ServerCodecs.class)
					&& getBeanFactory().isInstance(EurekaServerConfig.class)
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
	}

	private static class PeerAwareInstanceRegistryBuilder extends FactoryBeanDefinition {

		public PeerAwareInstanceRegistryBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		@Override
		public boolean isInstance() {
			return getBeanFactory().isInstance(ServerCodecs.class)
					&& getBeanFactory().isInstance(EurekaServerConfig.class)
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

	private static class ServerCodecsBuilder extends FactoryBeanDefinition {

		public ServerCodecsBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		@Override
		public boolean isInstance() {
			return getBeanFactory().isInstance(EurekaServerConfig.class);
		}

		@Override
		public Object create() throws BeansException {
			return new CloudServerCodecs(getBeanFactory().getInstance(EurekaServerConfig.class));
		}
	}
}

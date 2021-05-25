package scw.netflix.eureka.server;

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

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;

@Provider
public class EurekaServerBeanDefinitionLoader implements BeanDefinitionLoader {
	@Override
	public BeanDefinition load(ConfigurableBeanFactory beanFactory, Class<?> sourceClass, BeanDefinitionLoaderChain loaderChain) {
		if (sourceClass == ServerCodecs.class) {
			return new ServerCodecsBuilder(beanFactory, sourceClass);
		}

		if (sourceClass == PeerAwareInstanceRegistry.class) {
			return new PeerAwareInstanceRegistryBuilder(beanFactory, sourceClass);
		}

		if (sourceClass == PeerEurekaNodes.class) {
			return new PeerEurekaNodesBuilder(beanFactory, sourceClass);
		}

		if (sourceClass == EurekaServerContext.class) {
			return new EurekaServerContextBuilder(beanFactory, sourceClass);
		}

		if(sourceClass == EurekaServerConfig.class){
			return new EurekaServerConfigBuilder(beanFactory, sourceClass);
		}
		
		return loaderChain.load(beanFactory, sourceClass);
	}
	
	private static class EurekaServerConfigBuilder extends DefaultBeanDefinition{

		public EurekaServerConfigBuilder(ConfigurableBeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}
		
		@Override
		public boolean isInstance() {
			return beanFactory.isInstance(EurekaClientConfig.class);
		}
		
		@Override
		public Object create() throws BeansException {
			EurekaClientConfig clientConfig = beanFactory.getInstance(EurekaClientConfig.class);
			EurekaServerConfigBean server = new EurekaServerConfigBean();
			if (clientConfig.shouldRegisterWithEureka()) {
				// Set a sensible default if we are supposed to replicate
				server.setRegistrySyncRetries(5);
			}
			return server;
		}
	}

	private static class EurekaServerContextBuilder extends DefaultBeanDefinition {

		public EurekaServerContextBuilder(ConfigurableBeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
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

	private static ReplicationClientAdditionalFilters getReplicationClientAdditionalFilters(BeanFactory beanFactory) {
		if (!beanFactory.isInstance(ReplicationClientAdditionalFilters.class)) {
			return new ReplicationClientAdditionalFilters(Collections.emptySet());
		}
		return beanFactory.getInstance(ReplicationClientAdditionalFilters.class);
	}

	private static class PeerEurekaNodesBuilder extends DefaultBeanDefinition {

		public PeerEurekaNodesBuilder(ConfigurableBeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
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
	}

	private static class PeerAwareInstanceRegistryBuilder extends DefaultBeanDefinition {

		public PeerAwareInstanceRegistryBuilder(ConfigurableBeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
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

	private static class ServerCodecsBuilder extends DefaultBeanDefinition {

		public ServerCodecsBuilder(ConfigurableBeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		@Override
		public boolean isInstance() {
			return beanFactory.isInstance(EurekaServerConfig.class);
		}

		@Override
		public Object create() throws BeansException {
			return new CloudServerCodecs(beanFactory.getInstance(EurekaServerConfig.class));
		}
	}
}

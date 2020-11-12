package scw.eureka.server;

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
import scw.beans.BeanFactory;
import scw.beans.DefaultBeanDefinition;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE)
public class EurekaServerBeanBuilder implements BeanBuilderLoader {

	@Override
	public BeanDefinition loading(LoaderContext context, BeanBuilderLoaderChain loaderChain) {
		if (context.getTargetClass() == ServerCodecs.class) {
			return new ServerCodecsBuilder(context);
		}

		if (context.getTargetClass() == PeerAwareInstanceRegistry.class) {
			return new PeerAwareInstanceRegistryBuilder(context);
		}

		if (context.getTargetClass() == PeerEurekaNodes.class) {
			return new PeerEurekaNodesBuilder(context);
		}

		if (context.getTargetClass() == EurekaServerContext.class) {
			return new EurekaServerContextBuilder(context);
		}

		return loaderChain.loading(context);
	}

	private static class EurekaServerContextBuilder extends DefaultBeanDefinition {

		public EurekaServerContextBuilder(LoaderContext loaderContext) {
			super(loaderContext);
		}

		@Override
		public boolean isInstance() {
			return beanFactory.isInstance(EurekaServerConfig.class) && beanFactory.isInstance(ServerCodecs.class)
					&& beanFactory.isInstance(PeerAwareInstanceRegistry.class)
					&& beanFactory.isInstance(ApplicationInfoManager.class)
					&& beanFactory.isInstance(PeerEurekaNodes.class);
		}

		@Override
		public Object create() throws Exception {
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

		public PeerEurekaNodesBuilder(LoaderContext loaderContext) {
			super(loaderContext);
		}

		@Override
		public boolean isInstance() {
			return beanFactory.isInstance(ServerCodecs.class) && beanFactory.isInstance(EurekaServerConfig.class)
					&& beanFactory.isInstance(EurekaClientConfig.class)
					&& beanFactory.isInstance(ApplicationInfoManager.class)
					&& beanFactory.isInstance(PeerAwareInstanceRegistry.class);
		}

		@Override
		public Object create() throws Exception {
			PeerAwareInstanceRegistry registry = beanFactory.getInstance(PeerAwareInstanceRegistry.class);
			EurekaServerConfig serverConfig = beanFactory.getInstance(EurekaServerConfig.class);
			EurekaClientConfig clientConfig = beanFactory.getInstance(EurekaClientConfig.class);
			ServerCodecs serverCodecs = beanFactory.getInstance(ServerCodecs.class);
			ApplicationInfoManager applicationInfoManager = beanFactory.getInstance(ApplicationInfoManager.class);
			ReplicationClientAdditionalFilters replicationClientAdditionalFilters = getReplicationClientAdditionalFilters(
					beanFactory);
			return new RefreshablePeerEurekaNodes(registry, serverConfig, clientConfig, serverCodecs,
					applicationInfoManager, replicationClientAdditionalFilters);
		}
	}

	private static class PeerAwareInstanceRegistryBuilder extends DefaultBeanDefinition {

		public PeerAwareInstanceRegistryBuilder(LoaderContext loaderContext) {
			super(loaderContext);
		}

		@Override
		public boolean isInstance() {
			return beanFactory.isInstance(ServerCodecs.class) && beanFactory.isInstance(EurekaServerConfig.class)
					&& beanFactory.isInstance(EurekaClientConfig.class) && beanFactory.isInstance(EurekaClient.class)
					&& beanFactory.isInstance(InstanceRegistryProperties.class);
		}

		@Override
		public Object create() throws Exception {
			ServerCodecs serverCodecs = beanFactory.getInstance(ServerCodecs.class);
			EurekaServerConfig serverConfig = beanFactory.getInstance(EurekaServerConfig.class);
			EurekaClientConfig clientConfig = beanFactory.getInstance(EurekaClientConfig.class);
			EurekaClient eurekaClient = beanFactory.getInstance(EurekaClient.class);
			InstanceRegistryProperties instanceRegistryProperties = beanFactory
					.getInstance(InstanceRegistryProperties.class);
			return new InstanceRegistry(serverConfig, clientConfig, serverCodecs, eurekaClient,
					instanceRegistryProperties.getExpectedNumberOfClientsSendingRenews(),
					instanceRegistryProperties.getDefaultOpenForTrafficCount());
		}
	}

	private static class ServerCodecsBuilder extends DefaultBeanDefinition {

		public ServerCodecsBuilder(LoaderContext loaderContext) {
			super(loaderContext);
		}

		@Override
		public boolean isInstance() {
			return beanFactory.isInstance(EurekaServerConfig.class);
		}

		@Override
		public Object create() throws Exception {
			return new CloudServerCodecs(beanFactory.getInstance(EurekaServerConfig.class));
		}
	}
}

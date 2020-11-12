package scw.eureka.server;

import java.util.Set;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.cluster.PeerEurekaNode;
import com.netflix.eureka.cluster.PeerEurekaNodes;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;
import com.netflix.eureka.transport.JerseyReplicationClient;

public class RefreshablePeerEurekaNodes extends PeerEurekaNodes {

	private ReplicationClientAdditionalFilters replicationClientAdditionalFilters;

	public RefreshablePeerEurekaNodes(final PeerAwareInstanceRegistry registry, final EurekaServerConfig serverConfig,
			final EurekaClientConfig clientConfig, final ServerCodecs serverCodecs,
			final ApplicationInfoManager applicationInfoManager,
			final ReplicationClientAdditionalFilters replicationClientAdditionalFilters) {
		super(registry, serverConfig, clientConfig, serverCodecs, applicationInfoManager);
		this.replicationClientAdditionalFilters = replicationClientAdditionalFilters;
	}

	@Override
	protected PeerEurekaNode createPeerEurekaNode(String peerEurekaNodeUrl) {
		JerseyReplicationClient replicationClient = JerseyReplicationClient.createReplicationClient(serverConfig,
				serverCodecs, peerEurekaNodeUrl);

		this.replicationClientAdditionalFilters.getFilters().forEach(replicationClient::addReplicationClientFilter);

		String targetHost = hostFromUrl(peerEurekaNodeUrl);
		if (targetHost == null) {
			targetHost = "host";
		}
		return new PeerEurekaNode(registry, targetHost, peerEurekaNodeUrl, replicationClient, serverConfig);
	}

	/*
	 * Check whether specific properties have changed.
	 */
	protected boolean shouldUpdate(final Set<String> changedKeys) {
		assert changedKeys != null;

		// if eureka.client.use-dns-for-fetching-service-urls is true, then
		// service-url will not be fetched from environment.
		if (this.clientConfig.shouldUseDnsForFetchingServiceUrls()) {
			return false;
		}

		if (changedKeys.contains("eureka.client.region")) {
			return true;
		}

		for (final String key : changedKeys) {
			// property keys are not expected to be null.
			if (key.startsWith("eureka.client.service-url.") || key.startsWith("eureka.client.availability-zones.")) {
				return true;
			}
		}
		return false;
	}

}

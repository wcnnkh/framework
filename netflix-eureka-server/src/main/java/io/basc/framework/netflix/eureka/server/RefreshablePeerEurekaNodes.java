package io.basc.framework.netflix.eureka.server;

import java.util.Arrays;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.cluster.PeerEurekaNode;
import com.netflix.eureka.cluster.PeerEurekaNodes;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;
import com.netflix.eureka.transport.JerseyReplicationClient;

import io.basc.framework.env.Environment;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;

public class RefreshablePeerEurekaNodes extends PeerEurekaNodes
		implements EventListener<ChangeEvent<Elements<String>>> {
	private static final String[] KEYS = new String[] { "eureka.client.region*", "eureka.client.service-url.*",
			"eureka.client.availability-zones." };

	private ReplicationClientAdditionalFilters replicationClientAdditionalFilters;

	public RefreshablePeerEurekaNodes(final PeerAwareInstanceRegistry registry, final EurekaServerConfig serverConfig,
			final EurekaClientConfig clientConfig, final ServerCodecs serverCodecs,
			final ApplicationInfoManager applicationInfoManager,
			final ReplicationClientAdditionalFilters replicationClientAdditionalFilters, Environment environment) {
		super(registry, serverConfig, clientConfig, serverCodecs, applicationInfoManager);
		this.replicationClientAdditionalFilters = replicationClientAdditionalFilters;

		environment.getProperties().getKeyEventRegistry().registerListener(this);
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

	@Override
	public void onEvent(ChangeEvent<Elements<String>> event) {
		if (!event.getSource().anyMatch(() -> Arrays.asList(KEYS).stream(), StringUtils::equals)) {
			return;
		}

		if (clientConfig.shouldUseDnsForFetchingServiceUrls()) {
			return;
		}

		updatePeerEurekaNodes(resolvePeerUrls());
	}

}

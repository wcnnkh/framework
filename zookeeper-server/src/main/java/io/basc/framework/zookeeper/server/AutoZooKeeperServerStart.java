package io.basc.framework.zookeeper.server;

import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.Provider;

@Provider(order = Integer.MAX_VALUE)
public final class AutoZooKeeperServerStart implements ApplicationPostProcessor {
	private static final String DEFAULT_ZOOKEEPER_CONFIG = "zookeeper.properties";

	public void postProcessApplication(ConfigurableApplication application) throws Throwable {
		ZooKeeperServerStart start = null;
		if (application.getEnvironment().exists(DEFAULT_ZOOKEEPER_CONFIG)) {
			start = new ZooKeeperServerStart(
					application.getEnvironment().getProperties(DEFAULT_ZOOKEEPER_CONFIG).get());
		} else {
			Integer port = application.getEnvironment().getInteger("zookeeper.server.port");
			if (port != null) {
				start = new ZooKeeperServerStart(port);
			}
		}

		if (start != null) {
			start.start();
		}
	}
}

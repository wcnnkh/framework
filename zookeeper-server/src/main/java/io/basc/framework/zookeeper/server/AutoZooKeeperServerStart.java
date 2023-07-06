package io.basc.framework.zookeeper.server;

import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.Component;

@Component
public final class AutoZooKeeperServerStart implements ApplicationPostProcessor {
	private static final String DEFAULT_ZOOKEEPER_CONFIG = "zookeeper.properties";

	public void postProcessApplication(ConfigurableApplication application) throws Throwable {
		ZooKeeperServerStart start = null;
		if (application.getResourceLoader().exists(DEFAULT_ZOOKEEPER_CONFIG)) {
			start = new ZooKeeperServerStart(application.getProperties(DEFAULT_ZOOKEEPER_CONFIG).get());
		} else {
			Integer port = application.getProperties().getAsObject("zookeeper.server.port", Integer.class);
			if (port != null) {
				start = new ZooKeeperServerStart(port);
			}
		}

		if (start != null) {
			start.start();
		}
	}
}

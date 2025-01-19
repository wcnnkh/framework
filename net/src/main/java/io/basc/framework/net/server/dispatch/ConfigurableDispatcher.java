package io.basc.framework.net.server.dispatch;

import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.Server;
import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableDispatcher extends ConfigurableServices<Dispatcher> implements Dispatcher {
	public ConfigurableDispatcher() {
		setServiceClass(Dispatcher.class);
	}

	@Override
	public Server dispatch(ServerRequest request) {
		for (Dispatcher dispatcher : this) {
			Server service = dispatcher.dispatch(request);
			if (service != null) {
				return service;
			}
		}
		return null;
	}

}

package io.basc.framework.net.server.dispatch;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.Service;

public class ConfigurableDispatcher extends ConfigurableServices<Dispatcher> implements Dispatcher {
	public ConfigurableDispatcher() {
		setServiceClass(Dispatcher.class);
	}

	@Override
	public Service dispatch(ServerRequest request) {
		for (Dispatcher dispatcher : getServices()) {
			Service service = dispatcher.dispatch(request);
			if (service != null) {
				return service;
			}
		}
		return null;
	}

}

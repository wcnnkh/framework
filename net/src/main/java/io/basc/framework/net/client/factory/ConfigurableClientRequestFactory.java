package io.basc.framework.net.client.factory;

import java.io.IOException;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.net.client.ClientRequest;
import io.basc.framework.net.pattern.RequestPattern;

public class ConfigurableClientRequestFactory extends ConfigurableServices<ClientRequestFactory>
		implements ClientRequestFactory {

	public ConfigurableClientRequestFactory() {
		setServiceClass(ClientRequestFactory.class);
	}

	@Override
	public boolean canCreated(RequestPattern requestPattern) {
		return getServices().anyMatch((e) -> e.canCreated(requestPattern));
	}

	@Override
	public ClientRequest createRequest(RequestPattern requestPattern) throws IOException {
		for (ClientRequestFactory factory : getServices()) {
			if (factory.canCreated(requestPattern)) {
				return factory.createRequest(requestPattern);
			}
		}
		throw new UnsupportedException(requestPattern.toString());
	}

}

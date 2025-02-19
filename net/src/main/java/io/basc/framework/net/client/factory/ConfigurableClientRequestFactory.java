package io.basc.framework.net.client.factory;

import java.io.IOException;

import io.basc.framework.net.RequestPattern;
import io.basc.framework.net.client.ClientRequest;
import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableClientRequestFactory extends ConfigurableServices<ClientRequestFactory>
		implements ClientRequestFactory {

	public ConfigurableClientRequestFactory() {
		setServiceClass(ClientRequestFactory.class);
	}

	@Override
	public boolean canCreated(RequestPattern requestPattern) {
		return anyMatch((e) -> e.canCreated(requestPattern));
	}

	@Override
	public ClientRequest createRequest(RequestPattern requestPattern) throws IOException {
		for (ClientRequestFactory factory : this) {
			if (factory.canCreated(requestPattern)) {
				return factory.createRequest(requestPattern);
			}
		}
		throw new UnsupportedOperationException(requestPattern.toString());
	}

}

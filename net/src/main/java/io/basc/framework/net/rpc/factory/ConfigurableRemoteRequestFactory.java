package io.basc.framework.net.rpc.factory;

import java.io.IOException;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.Parameters;
import io.basc.framework.net.client.ClientRequest;
import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableRemoteRequestFactory extends ConfigurableServices<RemoteRequestFactory>
		implements RemoteRequestFactory {
	public ConfigurableRemoteRequestFactory() {
		setServiceClass(RemoteRequestFactory.class);
	}

	@Override
	public boolean test(Function function) {
		return anyMatch((e) -> e.test(function));
	}

	@Override
	public ClientRequest createRequest(Function function, Parameters parameters) throws IOException {
		for (RemoteRequestFactory factory : this) {
			if (factory.test(function)) {
				return factory.createRequest(function, parameters);
			}
		}
		throw new UnsupportedOperationException(function.toString());
	}

}

package io.basc.framework.net.rpc.factory;

import java.io.IOException;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.param.Parameters;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.net.client.ClientRequest;

public class ConfigurableRemoteRequestFactory extends ConfigurableServices<RemoteRequestFactory>
		implements RemoteRequestFactory {
	public ConfigurableRemoteRequestFactory() {
		setServiceClass(RemoteRequestFactory.class);
	}

	@Override
	public boolean test(Function function) {
		return getServices().anyMatch((e) -> e.test(function));
	}

	@Override
	public ClientRequest createRequest(Function function, Parameters parameters) throws IOException {
		for (RemoteRequestFactory factory : getServices()) {
			if (factory.test(function)) {
				return factory.createRequest(function, parameters);
			}
		}
		throw new UnsupportedException(function.toString());
	}

}

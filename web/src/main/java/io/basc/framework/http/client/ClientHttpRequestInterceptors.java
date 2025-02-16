package io.basc.framework.http.client;

import java.io.IOException;

import io.basc.framework.util.spi.ConfigurableServices;

public class ClientHttpRequestInterceptors extends ConfigurableServices<ClientHttpRequestInterceptor>
		implements ClientHttpRequestInterceptor {

	public ClientHttpRequestInterceptors() {
		setServiceClass(ClientHttpRequestInterceptor.class);
	}

	@Override
	public ClientHttpResponse intercept(ClientHttpRequest request, ClientHttpRequestExecutor chain) throws IOException {
		return new ClientHttpRequestInterceptorChain(iterator(), chain).execute(request);
	}
}

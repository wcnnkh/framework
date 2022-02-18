package io.basc.framework.http.client;

import java.io.IOException;

import io.basc.framework.factory.ConfigurableServices;

public class ClientHttpRequestInterceptors extends ConfigurableServices<ClientHttpRequestInterceptor>
		implements ClientHttpRequestInterceptor {

	public ClientHttpRequestInterceptors() {
		super(ClientHttpRequestInterceptor.class);
	}

	@Override
	public ClientHttpResponse intercept(ClientHttpRequest request, ClientHttpRequestExecutor chain) throws IOException {
		return new ClientHttpRequestInterceptorChain(iterator(), chain).execute(request);
	}
}

package io.basc.framework.net.server;

import java.io.IOException;

import io.basc.framework.beans.factory.config.ConfigurableServices;

public class ConfigurableFilter extends ConfigurableServices<Filter> implements Filter {

	public ConfigurableFilter() {
		setServiceClass(Filter.class);
	}

	@Override
	public void doFilter(ServerRequest request, ServerResponse response, Service chain)
			throws IOException, ServerException {
		FilterChain filterChain = new FilterChain(getServices().iterator(), chain);
		filterChain.service(request, response);
	}
}

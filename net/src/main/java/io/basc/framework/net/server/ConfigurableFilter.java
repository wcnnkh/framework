package io.basc.framework.net.server;

import java.io.IOException;

import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableFilter extends ConfigurableServices<Filter> implements Filter {

	public ConfigurableFilter() {
		setServiceClass(Filter.class);
	}

	@Override
	public void doFilter(ServerRequest request, ServerResponse response, Server chain)
			throws IOException, ServerException {
		FilterChain filterChain = new FilterChain(iterator(), chain);
		filterChain.service(request, response);
	}
}

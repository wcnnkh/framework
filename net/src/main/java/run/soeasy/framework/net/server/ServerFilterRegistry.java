package run.soeasy.framework.net.server;

import java.io.IOException;

import run.soeasy.framework.util.spi.ConfigurableServices;

public class ServerFilterRegistry extends ConfigurableServices<ServerFilter> implements ServerFilter {

	public ServerFilterRegistry() {
		setServiceClass(ServerFilter.class);
	}

	@Override
	public void doFilter(ServerRequest request, ServerResponse response, Service chain)
			throws IOException, ServerException {
		ServerFilterChain filterChain = new ServerFilterChain(iterator(), chain);
		filterChain.service(request, response);
	}
}

package io.basc.framework.net.server;

import java.io.IOException;

import io.basc.framework.net.RequestMapping;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Servers extends RequestMapping<Server> implements Server, ServerFilter {
	private final ServerFilterRegistry filters = new ServerFilterRegistry();

	@Override
	public void doFilter(ServerRequest request, ServerResponse response, Server server)
			throws IOException, ServerException {
		filters.doFilter(request, response, server);
	}

	@Override
	public void service(ServerRequest request, ServerResponse response) throws IOException, ServerException {
		Server server = dispatch(request);
		doFilter(request, response, server);
	}

}

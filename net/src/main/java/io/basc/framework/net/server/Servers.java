package io.basc.framework.net.server;

import java.io.IOException;

import io.basc.framework.net.pattern.RequestPattern;
import io.basc.framework.net.pattern.RequestPatternRegistry;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Servers extends RequestPatternRegistry<Server> implements Server, Filter {
	private final Filters filters = new Filters();

	public Server dispatch(ServerRequest request) {
		RequestPattern requestPattern = request.getPattern();
		Server server = get(requestPattern);
		if (server == null) {
			server = entries().filter((e) -> e.getKey().test(request)).map((e) -> e.getValue()).first();
		}
		return server;
	}

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

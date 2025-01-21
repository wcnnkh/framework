package io.basc.framework.net.server;

import java.io.IOException;

import io.basc.framework.net.pattern.PathPattern;
import io.basc.framework.net.pattern.RequestPatternRegistry;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispatcherServer extends RequestPatternRegistry<Server> implements Server {
	private final ConfigurableFilter configurableFilter = new ConfigurableFilter();
	/**
	 * 兜底服务，如果dispatcher找不到对应的服务那么执行此服务
	 */
	private Server bottomLineServer;

	public Server dispatch(ServerRequest request) {
		PathPattern pathPattern = request.getPattern();
		Server server = get(pathPattern);
		if (server == null) {
			server = entries().filter((e) -> e.getKey().test(request)).map((e) -> e.getValue()).first();
		}
		return server;
	}

	@Override
	public void service(ServerRequest request, ServerResponse response) throws IOException, ServerException {
		Server server = dispatch(request);
		if (server == null) {
			if (bottomLineServer != null) {
				bottomLineServer.service(request, response);
			}
			return;
		}

		configurableFilter.doFilter(request, response, server);
	}

}

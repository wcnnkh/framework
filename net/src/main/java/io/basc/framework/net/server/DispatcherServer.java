package io.basc.framework.net.server;

import java.io.IOException;

import lombok.Data;

@Data
public abstract class DispatcherServer implements Server {
	private final ConfigurableFilter configurableFilter = new ConfigurableFilter();
	/**
	 * 兜底服务，如果dispatcher找不到对应的服务那么执行此服务
	 */
	private Server bottomLineServer;

	public abstract Server dispatch(ServerRequest request);

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

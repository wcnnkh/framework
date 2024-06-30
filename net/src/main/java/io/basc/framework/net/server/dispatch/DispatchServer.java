package io.basc.framework.net.server.dispatch;

import java.io.IOException;

import io.basc.framework.net.server.ConfigurableFilter;
import io.basc.framework.net.server.ServerException;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerResponse;
import io.basc.framework.net.server.Service;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispatchServer implements Service {
	private final ConfigurableDispatcher dispatcher = new ConfigurableDispatcher();
	private final ConfigurableFilter filter = new ConfigurableFilter();
	/**
	 * 兜底服务，如果dispatcher找不到对应的服务那么执行此服务
	 */
	private Service bottomLineService;

	@Override
	public void service(ServerRequest request, ServerResponse response) throws IOException, ServerException {
		Service service = dispatcher.dispatch(request);
		if (service == null) {
			if (bottomLineService != null) {
				bottomLineService.service(request, response);
			}
			return;
		}

		filter.doFilter(request, response, service);
	}

}

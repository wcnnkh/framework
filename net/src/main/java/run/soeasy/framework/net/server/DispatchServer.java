package run.soeasy.framework.net.server;

import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.net.RequestMapping;

@Getter
@Setter
public class DispatchServer extends RequestMapping<Service> implements Service {
	private final ServerFilterRegistry filters = new ServerFilterRegistry();
	private ErrorHandler errorHandler;
	private GroundService groundService;

	@Override
	public void service(ServerRequest request, ServerResponse response) throws IOException, ServerException {
		Service service = dispatch(request);
		if (service == null) {
			service = groundService;
		}

		try {
			filters.doFilter(request, response, service);
		} catch (IOException e) {
			throw e;
		} catch (Throwable e) {
			if (errorHandler == null) {
				throw new ServerException(e);
			}
			errorHandler.doError(request, response, service, e);
		}
	}

}

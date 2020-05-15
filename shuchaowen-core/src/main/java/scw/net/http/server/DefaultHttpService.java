package scw.net.http.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

public class DefaultHttpService implements HttpService {
	protected final LinkedList<HttpServiceHandler> handlers = new LinkedList<HttpServiceHandler>();
	protected final LinkedList<HttpServiceFilter> filters = new LinkedList<HttpServiceFilter>();

	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		FiltersHttpServer server = new FiltersHttpServer();
		server.service(request, response);
	}

	protected void doHandle(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		for (HttpServiceHandler handler : handlers) {
			if (handler.accept(request)) {
				handler.doHandle(request, response);
				return;
			}
		}
	}

	public LinkedList<HttpServiceHandler> getHandlers() {
		return handlers;
	}

	public LinkedList<HttpServiceFilter> getFilters() {
		return filters;
	}

	private final class FiltersHttpServer implements HttpService {
		private Iterator<HttpServiceFilter> iterator = filters.iterator();

		public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
			if (iterator.hasNext()) {
				iterator.next().doFilter(request, response, FiltersHttpServer.this);
			} else {
				doHandle(request, response);
			}
		}
	}
}

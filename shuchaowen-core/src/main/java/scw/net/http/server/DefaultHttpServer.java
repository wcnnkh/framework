package scw.net.http.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;

import scw.lang.NestedRuntimeException;
import scw.net.http.server.exception.HttpServerExceptionHandler;
import scw.net.http.server.filter.HttpServerFilter;
import scw.net.http.server.handler.HttpServerHandler;

public class DefaultHttpServer implements HttpServer {
	private final TreeSet<HttpServerHandler> handlers = new TreeSet<HttpServerHandler>();
	private final TreeSet<HttpServerFilter> filters = new TreeSet<HttpServerFilter>();
	private final TreeSet<HttpServerExceptionHandler> exceptionHandlers = new TreeSet<HttpServerExceptionHandler>();

	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		FiltersHttpServer server = new FiltersHttpServer();
		server.service(request, response);
	}

	protected Object doHandle(ServerHttpRequest request, ServerHttpResponse response) throws Throwable {
		for (HttpServerHandler handler : handlers) {
			if (handler.isSupports(request)) {
				return handler.handler(request, response);
			}
		}
		return lastHandler(request, response);
	}

	protected Object lastHandler(ServerHttpRequest request, ServerHttpResponse response) throws Throwable {
		return null;
	}

	protected Object doError(ServerHttpRequest request, ServerHttpResponse response, Throwable error)
			throws IOException {
		for (HttpServerExceptionHandler exceptionHandler : exceptionHandlers) {
			if (exceptionHandler.isSupport(request, error)) {
				return exceptionHandler.handle(request, response, error);
			}
		}
		return lastExceptionHandler(request, response, error);
	}

	protected Object lastExceptionHandler(ServerHttpRequest request, ServerHttpResponse response, Throwable error)
			throws IOException {
		throw new NestedRuntimeException(error);
	}

	public void output(ServerHttpRequest request, ServerHttpResponse response, Object message) throws IOException {

	}

	private final class FiltersHttpServer implements HttpServer {
		private Iterator<HttpServerFilter> iterator = filters.iterator();

		public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
			Object message;
			if (iterator.hasNext()) {
				try {
					message = iterator.next().doFilter(request, response, FiltersHttpServer.this);
				} catch (Throwable e) {
					message = doError(request, response, e);
				}
			} else {
				try {
					message = doHandle(request, response);
				} catch (Throwable e) {
					message = doError(request, response, e);
				}
			}
			output(request, response, message);
		}

	}
}

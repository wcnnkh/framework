package scw.http.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.http.server.cors.CorsFilter;
import scw.http.server.resource.DefaultHttpServerResourceFactory;
import scw.http.server.resource.HttpServerResourceFactory;
import scw.http.server.resource.HttpServerResourceHandler;
import scw.http.server.rpc.HttpServerRpcHandler;
import scw.value.property.PropertyFactory;

public class DefaultHttpService implements HttpService {
	protected final LinkedList<HttpServiceHandler> handlers = new LinkedList<HttpServiceHandler>();
	protected final LinkedList<HttpServiceFilter> filters = new LinkedList<HttpServiceFilter>();

	public DefaultHttpService() {
	}

	public DefaultHttpService(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		HttpServerResourceFactory httpServerResourceFactory = beanFactory.isInstance(HttpServerResourceFactory.class)
				? beanFactory.getInstance(HttpServerResourceFactory.class)
				: new DefaultHttpServerResourceFactory(propertyFactory);
		HttpServerResourceHandler resourceHandler = new HttpServerResourceHandler(httpServerResourceFactory);
		handlers.add(resourceHandler);
		handlers.add(new HttpServerRpcHandler(beanFactory, propertyFactory));

		filters.add(new CorsFilter(beanFactory, propertyFactory));
		filters.addAll(InstanceUtils.getConfigurationList(HttpServiceFilter.class, beanFactory, propertyFactory));
		handlers.addAll(InstanceUtils.getConfigurationList(HttpServiceHandler.class, beanFactory, propertyFactory));
	}

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

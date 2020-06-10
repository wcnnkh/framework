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
		filters.add(new CorsFilter(beanFactory, propertyFactory));
		filters.addAll(InstanceUtils.getConfigurationList(HttpServiceFilter.class, beanFactory, propertyFactory));
		handlers.addAll(InstanceUtils.getConfigurationList(HttpServiceHandler.class, beanFactory, propertyFactory));
	}

	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		FiltersHttpService service = new FiltersHttpService();
		try {
			service.service(request, response);
		} finally {
			if (!response.isCommitted()) {
				if (request.isSupportAsyncControl()) {
					ServerHttpAsyncControl serverHttpAsyncControl = request.getAsyncControl(response);
					if (serverHttpAsyncControl.isStarted()) {
						serverHttpAsyncControl.addListener(new ServerHttpResponseAsyncFlushListener(response));
						return;
					}
				}

				response.flush();
			}
		}
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

	private final class FiltersHttpService implements HttpService {
		private Iterator<HttpServiceFilter> iterator = filters.iterator();

		public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
			if (iterator.hasNext()) {
				iterator.next().doFilter(request, response, FiltersHttpService.this);
			} else {
				doHandle(request, response);
			}
		}
	}
}

package scw.mvc.servlet;

import java.io.IOException;

import scw.beans.BeanFactory;
import scw.core.instance.annotation.Configuration;
import scw.http.server.HttpServiceHandler;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.mvc.HttpChannel;
import scw.mvc.HttpControllerHandler;
import scw.net.NetworkUtils;
import scw.servlet.http.ServletServerHttpRequest;
import scw.servlet.http.ServletServerHttpResponse;
import scw.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE, value = HttpServiceHandler.class)
public class HttpServletControllerHandler extends HttpControllerHandler {

	public HttpServletControllerHandler(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		super(beanFactory, propertyFactory);
	}

	@Override
	public boolean accept(ServerHttpRequest request) {
		return (request instanceof ServletServerHttpRequest);
	}

	@Override
	protected HttpChannel createHttpChannel(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
		ServletServerHttpResponse servletServerHttpResponse = (ServletServerHttpResponse) response;
		if (NetworkUtils.isJsonMessage(request)) {
			return new ServletJsonHttpChannel(beanFactory, getJsonSupport(), servletServerHttpRequest,
					servletServerHttpResponse);
		} else {
			return new ServletFormHttpChannel(beanFactory, getJsonSupport(), servletServerHttpRequest,
					servletServerHttpResponse);
		}
	}
}

package scw.mvc.servlet;

import java.io.IOException;

import scw.beans.BeanFactory;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.mvc.DefaultHttpChannelFactory;
import scw.mvc.HttpChannel;
import scw.servlet.http.ServletServerHttpRequest;
import scw.servlet.http.ServletServerHttpResponse;

public class DefaultServletHttpChannelFactory extends DefaultHttpChannelFactory {

	static {
		ServletServerHttpRequest.class.getName();
		ServletServerHttpResponse.class.getName();
	}

	public DefaultServletHttpChannelFactory(BeanFactory beanFactory) {
		super(beanFactory);
	}

	public HttpChannel create(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
		ServletServerHttpResponse servletServerHttpResponse = (ServletServerHttpResponse) response;
		return new DefaultServletHttpChannel(beanFactory, getJsonSupport(), servletServerHttpRequest,
				servletServerHttpResponse);
	}
}

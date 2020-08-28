package scw.mvc.servlet;

import java.io.IOException;

import scw.beans.BeanFactory;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.json.JSONSupport;
import scw.mvc.DefaultHttpChannelFactory;
import scw.mvc.HttpChannel;
import scw.servlet.ServletUtils;
import scw.servlet.http.ServletServerHttpRequest;
import scw.servlet.http.ServletServerHttpResponse;

public class DefaultServletHttpChannelFactory extends DefaultHttpChannelFactory {
	static {
		ServletServerHttpRequest.class.getName();
		ServletServerHttpResponse.class.getName();
	}

	public DefaultServletHttpChannelFactory(BeanFactory beanFactory, JSONSupport jsonSupport) {
		super(beanFactory, jsonSupport);
	}

	public HttpChannel create(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		ServletServerHttpRequest servletServerHttpRequest = ServletUtils.getServletServerHttpRequest(request);
		ServletServerHttpResponse servletServerHttpResponse = ServletUtils.getServletServerHttpResponse(response);
		if (servletServerHttpRequest == null || servletServerHttpResponse == null) {
			return super.create(servletServerHttpRequest, servletServerHttpResponse);
		}
		return new DefaultServletHttpChannel(beanFactory, getJsonSupport(), request, response,
				servletServerHttpRequest.getHttpServletRequest(), servletServerHttpResponse.getHttpServletResponse());
	}
}

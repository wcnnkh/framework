package scw.mvc.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.json.JSONSupport;
import scw.net.NetworkUtils;
import scw.net.http.server.mvc.FormHttpChannel;
import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.JsonHttpChannel;
import scw.net.http.server.servlet.ServletServerHttpRequest;
import scw.net.http.server.servlet.ServletServerHttpResponse;

public class DefaultHttpServletChannelFactory implements HttpServletChannelFactory {
	private BeanFactory beanFactory;
	private JSONSupport jsonSupport;

	public DefaultHttpServletChannelFactory(BeanFactory beanFactory, JSONSupport jsonSupport) {
		this.beanFactory = beanFactory;
		this.jsonSupport = jsonSupport;
	}

	public HttpChannel getHttpChannel(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		ServletServerHttpRequest httpRequest = new ServletServerHttpRequest(httpServletRequest);
		ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(httpServletResponse);
		if (NetworkUtils.isJsonMessage(httpRequest)) {
			return new JsonHttpChannel<ServletServerHttpRequest, ServletServerHttpResponse>(beanFactory, jsonSupport,
					httpRequest, httpResponse);
		} else {
			return new FormHttpChannel<ServletServerHttpRequest, ServletServerHttpResponse>(beanFactory, jsonSupport,
					httpRequest, httpResponse);
		}
	}
}

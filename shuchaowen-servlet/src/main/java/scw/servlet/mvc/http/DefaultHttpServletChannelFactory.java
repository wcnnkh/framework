package scw.servlet.mvc.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.json.JSONSupport;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;

public class DefaultHttpServletChannelFactory implements HttpServletChannelFactory {
	private BeanFactory beanFactory;
	private boolean cookieValue;
	private JSONSupport jsonSupport;

	public DefaultHttpServletChannelFactory(BeanFactory beanFactory, JSONSupport jsonSupport,
			boolean cookieValue) {
		this.beanFactory = beanFactory;
		this.cookieValue = cookieValue;
		this.jsonSupport = jsonSupport;
	}

	public HttpChannel getHttpChannel(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		MyHttpServletRequest httpRequest = new MyHttpServletRequest(httpServletRequest);
		MyHttpServletResponse httpResponse = new MyHttpServletResponse(httpServletResponse);
		if (MVCUtils.isJsonRequest(httpRequest)) {
			return new JsonHttpServletChannel(beanFactory, jsonSupport, cookieValue, httpRequest, httpResponse);
		} else {
			return new FormHttpServletChannel(beanFactory, jsonSupport, cookieValue, httpRequest, httpResponse);
		}
	}
}
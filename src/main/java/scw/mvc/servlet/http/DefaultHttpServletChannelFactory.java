package scw.mvc.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.json.JsonSupport;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;

public final class DefaultHttpServletChannelFactory implements HttpServletChannelFactory {
	private BeanFactory beanFactory;
	private boolean cookieValue;
	private JsonSupport jsonParseSupport;
	private String jsonp;

	public DefaultHttpServletChannelFactory(BeanFactory beanFactory, JsonSupport jsonParseSupport,
			boolean cookieValue, String jsonp) {
		this.beanFactory = beanFactory;
		this.cookieValue = cookieValue;
		this.jsonParseSupport = jsonParseSupport;
		this.jsonp = jsonp;
	}

	public HttpChannel getHttpChannel(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		HttpRequest httpRequest = new MyHttpServletRequest(httpServletRequest);
		HttpResponse httpResponse = new MyHttpServletResponse(httpServletResponse);
		if (MVCUtils.isJsonRequest(httpRequest)) {
			return new JsonHttpServletChannel(beanFactory, jsonParseSupport, cookieValue, httpRequest, httpResponse,
					jsonp);
		} else {
			return new FormHttpServletChannel(beanFactory, jsonParseSupport, cookieValue, httpRequest, httpResponse,
					jsonp);
		}
	}
}

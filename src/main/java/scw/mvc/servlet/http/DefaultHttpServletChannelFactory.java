package scw.mvc.servlet.http;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.json.JSONParseSupport;
import scw.mvc.MVCUtils;
import scw.mvc.ParameterFilter;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;

public final class DefaultHttpServletChannelFactory implements HttpServletChannelFactory {
	private BeanFactory beanFactory;
	private Collection<ParameterFilter> parameterFilters;
	private boolean cookieValue;
	private boolean logEnabled;
	private JSONParseSupport jsonParseSupport;

	public DefaultHttpServletChannelFactory(BeanFactory beanFactory, boolean logEnabled,
			Collection<ParameterFilter> parameterFilters, JSONParseSupport jsonParseSupport, boolean cookieValue) {
		this.beanFactory = beanFactory;
		this.logEnabled = logEnabled;
		this.parameterFilters = parameterFilters;
		this.cookieValue = cookieValue;
		this.jsonParseSupport = jsonParseSupport;
	}

	public HttpChannel getHttpChannel(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		HttpRequest httpRequest = new MyHttpServletRequest(httpServletRequest);
		HttpResponse httpResponse = new MyHttpServletResponse(httpServletResponse);
		if (MVCUtils.isJsonRequest(httpRequest)) {
			return new JsonHttpServletChannel(beanFactory, logEnabled, parameterFilters, jsonParseSupport, cookieValue,
					httpRequest, httpResponse);
		} else {
			return new FormHttpServletChannel(beanFactory, logEnabled, parameterFilters, jsonParseSupport, cookieValue,
					httpRequest, httpResponse);

		}
	}
}

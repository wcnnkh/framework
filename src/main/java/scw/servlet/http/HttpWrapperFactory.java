package scw.servlet.http;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.core.json.JSONParseSupport;
import scw.servlet.AbstractWrapperFactory;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.ServletUtils;
import scw.servlet.beans.RequestBeanFactory;

public class HttpWrapperFactory extends AbstractWrapperFactory {
	private final boolean cookieValue;
	private final JSONParseSupport jsonParseSupport;
	private boolean jsonp;

	public HttpWrapperFactory(RequestBeanFactory requestBeanFactory, boolean debug, boolean cookieValue,
			JSONParseSupport jsonParseSupport, boolean jsonp) {
		super(requestBeanFactory, debug);
		this.cookieValue = cookieValue;
		this.jsonParseSupport = jsonParseSupport;
		this.jsonp = jsonp;
	}

	public Request wrapperRequest(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		if (ServletUtils.isJsonRequest(httpServletRequest)) {
			return new JSONHttpRequest(getRequestBeanFactory(), httpServletRequest, cookieValue, jsonParseSupport,
					isDebug());
		} else {
			return new FormHttpRequest(getRequestBeanFactory(), httpServletRequest, cookieValue, isDebug());
		}
	}

	public Response wrapperResponse(ServletRequest request, ServletResponse response) throws Exception {
		return new DefaultHttpResponse(jsonParseSupport, (HttpRequest) request, (HttpServletResponse) response, jsonp,
				isDebug());
	}

}

package scw.servlet.mvc.http;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import scw.beans.BeanFactory;
import scw.core.parameter.ParameterConfig;
import scw.json.JSONSupport;
import scw.mvc.http.AbstractHttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;

@SuppressWarnings("unchecked")
public abstract class HttpServletChannel extends AbstractHttpChannel {

	public HttpServletChannel(BeanFactory beanFactory, JSONSupport jsonParseSupport, boolean cookieValue,
			HttpRequest request, HttpResponse response, String jsonp) {
		super(beanFactory, jsonParseSupport, cookieValue, request, response, jsonp);
	}

	@Override
	public Object getParameter(ParameterConfig parameterConfig) {
		if (ServletRequest.class.isAssignableFrom(parameterConfig.getType())) {
			return getRequest().getHttpServletRequest();
		} else if (ServletResponse.class.isAssignableFrom(parameterConfig.getType())) {
			return getResponse();
		} else if (HttpSession.class == parameterConfig.getType()) {
			return getRequest().getHttpServletRequest().getSession();
		}
		return super.getParameter(parameterConfig);
	}

	@Override
	public MyHttpServletRequest getRequest() {
		return (MyHttpServletRequest) super.getRequest();
	}

	@Override
	public MyHttpServletResponse getResponse() {
		return (MyHttpServletResponse) super.getResponse();
	}
}

package scw.mvc.servlet.http;

import java.util.Collection;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import scw.beans.BeanFactory;
import scw.core.parameter.ParameterConfig;
import scw.json.JSONParseSupport;
import scw.mvc.http.AbstractHttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.parameter.ParameterFilter;

@SuppressWarnings("unchecked")
public abstract class HttpServletChannel extends AbstractHttpChannel {

	public HttpServletChannel(BeanFactory beanFactory, boolean logEnabled, Collection<ParameterFilter> parameterFilters,
			JSONParseSupport jsonParseSupport, boolean cookieValue, HttpRequest request, HttpResponse response, String jsonp) {
		super(beanFactory, logEnabled, parameterFilters, jsonParseSupport, cookieValue, request, response, jsonp);
	}

	@Override
	public Object getParameter(ParameterConfig parameterConfig) {
		if (ServletRequest.class.isAssignableFrom(parameterConfig.getType())) {
			return getRequest();
		} else if (ServletResponse.class.isAssignableFrom(parameterConfig.getType())) {
			return getResponse();
		} else if (HttpSession.class == parameterConfig.getType()) {
			return getRequest().getSession();
		} else if (HttpServletParameterRequest.class == parameterConfig.getType()) {
			return new HttpServletParameterRequest(this, getRequest());
		}

		return super.getParameter(parameterConfig);
	}

	@Override
	public MyHttpServletRequest getRequest() {
		return super.getRequest();
	}

	@Override
	public MyHttpServletResponse getResponse() {
		return super.getResponse();
	}
}

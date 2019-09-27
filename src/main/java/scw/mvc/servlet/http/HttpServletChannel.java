package scw.mvc.servlet.http;

import java.util.Collection;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import scw.beans.BeanFactory;
import scw.json.JSONParseSupport;
import scw.mvc.ParameterDefinition;
import scw.mvc.ParameterFilter;
import scw.mvc.http.AbstractHttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;

@SuppressWarnings("unchecked")
public abstract class HttpServletChannel extends AbstractHttpChannel {

	public HttpServletChannel(BeanFactory beanFactory, boolean logEnabled, Collection<ParameterFilter> parameterFilters,
			JSONParseSupport jsonParseSupport, boolean cookieValue, HttpRequest request, HttpResponse response, String jsonp) {
		super(beanFactory, logEnabled, parameterFilters, jsonParseSupport, cookieValue, request, response, jsonp);
	}

	@Override
	public Object getParameter(ParameterDefinition parameterDefinition) {
		if (ServletRequest.class.isAssignableFrom(parameterDefinition.getType())) {
			return getRequest();
		} else if (ServletResponse.class.isAssignableFrom(parameterDefinition.getType())) {
			return getResponse();
		} else if (HttpSession.class == parameterDefinition.getType()) {
			return getRequest().getSession();
		} else if (HttpServletParameterRequest.class == parameterDefinition.getType()) {
			return new HttpServletParameterRequest(this, getRequest());
		}

		return super.getParameter(parameterDefinition);
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

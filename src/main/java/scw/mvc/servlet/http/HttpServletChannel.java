package scw.mvc.servlet.http;

import java.util.Collection;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import scw.beans.BeanFactory;
import scw.json.JSONParseSupport;
import scw.mvc.MVCUtils;
import scw.mvc.ParameterDefinition;
import scw.mvc.ParameterFilter;
import scw.mvc.http.AbstractHttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.servlet.ServletUtils;

public abstract class HttpServletChannel extends AbstractHttpChannel {

	public HttpServletChannel(BeanFactory beanFactory, boolean logEnabled, Collection<ParameterFilter> parameterFilters,
			JSONParseSupport jsonParseSupport, boolean cookieValue, HttpRequest request, HttpResponse response) {
		super(beanFactory, logEnabled, parameterFilters, jsonParseSupport, cookieValue, request, response);
	}

	@Override
	public Object getParameter(ParameterDefinition parameterDefinition) {
		if (ServletRequest.class.isAssignableFrom(parameterDefinition.getType())) {
			return getRequest();
		} else if (ServletResponse.class.isAssignableFrom(parameterDefinition.getType())) {
			return getResponse();
		} else if (HttpSession.class == parameterDefinition.getType()) {
			return getRequest().getSession();
		}

		return super.getParameter(parameterDefinition);
	}

	@SuppressWarnings("unchecked")
	@Override
	public MyHttpServletRequestWrapper getRequest() {
		return super.getRequest();
	}

	@SuppressWarnings("unchecked")
	@Override
	public MyHttpServletResponseWrapper getResponse() {
		return super.getResponse();
	}

	@Override
	public void write(Object obj) throws Throwable {
		if (obj == null) {
			return;
		}

		if (obj instanceof String) {
			String redirect = MVCUtils.parseRedirect((String) obj, true);
			if (redirect != null) {
				redirect = ServletUtils.formatContextPathUrl(getRequest(), redirect);
				getResponse().sendRedirect(redirect);
				return ;
			}
		}

		super.write(obj);
	}
}

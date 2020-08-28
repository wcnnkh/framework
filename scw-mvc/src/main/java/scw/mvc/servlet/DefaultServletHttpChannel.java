package scw.mvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.core.parameter.ParameterDescriptor;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.json.JSONSupport;
import scw.mvc.DefaultHttpChannel;

public class DefaultServletHttpChannel extends DefaultHttpChannel {
	private final HttpServletRequest httpServletRequest;
	private final HttpServletResponse httpServletResponse;

	public DefaultServletHttpChannel(BeanFactory beanFactory, JSONSupport jsonParseSupport, ServerHttpRequest request,
			ServerHttpResponse response, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		super(beanFactory, jsonParseSupport, request, response);
		this.httpServletRequest = httpServletRequest;
		this.httpServletResponse = httpServletResponse;
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		if (HttpServletRequest.class.isAssignableFrom(parameterDescriptor.getType())) {
			return getHttpServletRequest();
		} else if (HttpServletResponse.class.isAssignableFrom(parameterDescriptor.getType())) {
			return getHttpServletRequest();
		}
		return super.getParameter(parameterDescriptor);
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public HttpServletResponse getHttpServletResponse() {
		return httpServletResponse;
	}
}

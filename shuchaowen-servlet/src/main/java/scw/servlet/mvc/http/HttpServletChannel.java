package scw.servlet.mvc.http;

import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import scw.beans.BeanFactory;
import scw.core.parameter.ParameterConfig;
import scw.json.JSONSupport;
import scw.mvc.AsyncControl;
import scw.mvc.http.AbstractHttpChannel;

@SuppressWarnings("unchecked")
public abstract class HttpServletChannel extends AbstractHttpChannel {
	
	public HttpServletChannel(BeanFactory beanFactory, JSONSupport jsonParseSupport, boolean cookieValue,
			MyHttpServletRequest request, MyHttpServletResponse response) {
		super(beanFactory, jsonParseSupport, cookieValue, request, response);
	}

	@Override
	public Object getParameter(ParameterConfig parameterConfig) {
		if (ServletRequest.class.isAssignableFrom(parameterConfig.getType())) {
			return getHttpServletRequest();
		} else if (ServletResponse.class.isAssignableFrom(parameterConfig.getType())) {
			return getHttpServletResponse();
		} else if (HttpSession.class == parameterConfig.getType()) {
			return getRequest().getHttpServletRequest().getSession();
		}
		return super.getParameter(parameterConfig);
	}
	
	public HttpServletRequest getHttpServletRequest(){
		return getRequest().getHttpServletRequest();
	}
	
	public HttpServletResponse getHttpServletResponse(){
		return getResponse().getHttpServletResponse();
	}
	
	@Override
	public Object getAttribute(String name) {
		return getHttpServletRequest().getAttribute(name);
	}
	
	@Override
	public Enumeration<String> getAttributeNames() {
		return getHttpServletRequest().getAttributeNames();
	}

	@Override
	public void setAttribute(String name, Object o) {
		getHttpServletRequest().setAttribute(name, o);
	}
	
	@Override
	public void removeAttribute(String name) {
		getHttpServletRequest().removeAttribute(name);
	}

	@Override
	public MyHttpServletRequest getRequest() {
		return (MyHttpServletRequest) super.getRequest();
	}

	@Override
	public MyHttpServletResponse getResponse() {
		return (MyHttpServletResponse) super.getResponse();
	}

	public boolean isSupportAsyncControl() {
		return getRequest().getHttpServletRequest().isAsyncSupported();
	}

	public AsyncControl getAsyncControl() {
		return new HttpServletAsyncControl(getRequest(), getResponse());
	}
}

package scw.servlet.mvc.http;

import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
			return getServletRequest();
		} else if (ServletResponse.class.isAssignableFrom(parameterConfig.getType())) {
			return getServletResponse();
		} else if (HttpSession.class == parameterConfig.getType()) {
			return getRequest().getHttpServletRequest().getSession();
		}
		return super.getParameter(parameterConfig);
	}
	
	public ServletRequest getServletRequest(){
		return getRequest().getHttpServletRequest();
	}
	
	public ServletResponse getServletResponse(){
		return getResponse();
	}
	
	@Override
	public Object getAttribute(String name) {
		return getServletRequest().getAttribute(name);
	}
	
	@Override
	public Enumeration<String> getAttributeNames() {
		return getServletRequest().getAttributeNames();
	}

	@Override
	public void setAttribute(String name, Object o) {
		getServletRequest().setAttribute(name, o);
	}
	
	@Override
	public void removeAttribute(String name) {
		getServletRequest().removeAttribute(name);
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

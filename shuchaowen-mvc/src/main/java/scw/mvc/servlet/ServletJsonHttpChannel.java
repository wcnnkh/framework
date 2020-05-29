package scw.mvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.core.parameter.ParameterDescriptor;
import scw.json.JSONSupport;
import scw.mvc.JsonHttpChannel;
import scw.servlet.http.ServletServerHttpRequest;
import scw.servlet.http.ServletServerHttpResponse;

public class ServletJsonHttpChannel extends JsonHttpChannel<ServletServerHttpRequest, ServletServerHttpResponse>{

	public ServletJsonHttpChannel(BeanFactory beanFactory, JSONSupport jsonParseSupport,
			ServletServerHttpRequest request, ServletServerHttpResponse response) {
		super(beanFactory, jsonParseSupport, request, response);
	}
	
	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		if(HttpServletRequest.class.isAssignableFrom(parameterDescriptor.getType())){
			return ((ServletServerHttpRequest)getRequest()).getHttpServletRequest();
		}else if(HttpServletResponse.class.isAssignableFrom(parameterDescriptor.getType())){
			return ((ServletServerHttpResponse)getResponse()).getHttpServletResponse();
		}
		return super.getParameter(parameterDescriptor);
	}
}

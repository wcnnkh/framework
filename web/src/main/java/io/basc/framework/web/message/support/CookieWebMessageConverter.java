package io.basc.framework.web.message.support;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

public class CookieWebMessageConverter implements WebMessageConverter{

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object read(ServerHttpRequest request,
			ParameterDescriptor parameterDescriptor) throws IOException,
			WebMessagelConverterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object read(ClientHttpResponse response,
			TypeDescriptor typeDescriptor) throws IOException,
			WebMessagelConverterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response,
			TypeDescriptor typeDescriptor, Object body) throws IOException,
			WebMessagelConverterException {
		// TODO Auto-generated method stub
		
	}

}

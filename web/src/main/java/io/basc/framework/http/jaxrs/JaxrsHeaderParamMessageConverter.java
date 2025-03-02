package io.basc.framework.http.jaxrs;

import java.io.IOException;

import javax.ws.rs.HeaderParam;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.support.AbstractHeaderWebMessageConverter;

public class JaxrsHeaderParamMessageConverter extends AbstractHeaderWebMessageConverter {

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		return descriptor.isAnnotationPresent(HeaderParam.class);
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		HeaderParam param = parameterDescriptor.getTypeDescriptor().getAnnotation(HeaderParam.class);
		if (param == null || StringUtils.isEmpty(param.value())) {
			return super.read(request, parameterDescriptor);
		}
		return super.read(request, parameterDescriptor.rename(param.value()));
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		return typeDescriptor.isAnnotationPresent(HeaderParam.class);
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		HeaderParam param = parameterDescriptor.getTypeDescriptor().getAnnotation(HeaderParam.class);
		if (param == null || StringUtils.isEmpty(param.value())) {
			return super.write(request, parameterDescriptor, parameter);
		}
		return super.write(request, parameterDescriptor.rename(param.value()), parameter);
	}
}

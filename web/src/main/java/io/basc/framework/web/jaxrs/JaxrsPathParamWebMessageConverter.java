package io.basc.framework.web.jaxrs;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.support.AbstractPathParamWebMessageConverter;

import java.io.IOException;

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

@Provider
public class JaxrsPathParamWebMessageConverter extends AbstractPathParamWebMessageConverter {

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		return descriptor.isAnnotationPresent(FormParam.class);
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		PathParam param = parameterDescriptor.getAnnotation(PathParam.class);
		if (param == null || StringUtils.isEmpty(param.value())) {
			return super.read(request, parameterDescriptor);
		}
		return super.read(request, parameterDescriptor.rename(param.value()));
	}

	@Override
	public UriComponentsBuilder write(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		PathParam param = parameterDescriptor.getAnnotation(PathParam.class);
		if (param == null || StringUtils.isEmpty(param.value())) {
			return super.write(builder, parameterDescriptor, parameter);
		}
		return super.write(builder, parameterDescriptor.rename(param.value()), parameter);
	}

	@Override
	public boolean canWrite(TypeDescriptor typeDescriptor, Object parameter) {
		return typeDescriptor.isAnnotationPresent(PathParam.class);
	}

}

package io.basc.framework.context.jaxrs;

import java.io.IOException;

import javax.ws.rs.QueryParam;

import io.basc.framework.context.annotation.Component;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.support.AbstractParamWebMessageConverter;

@Component
public class JaxrsQueryParamWebMessageConverter extends AbstractParamWebMessageConverter {

	@Override
	public boolean canRead(HttpMessage message, TypeDescriptor descriptor) {
		return descriptor.isAnnotationPresent(QueryParam.class);
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		QueryParam param = parameterDescriptor.getTypeDescriptor().getAnnotation(QueryParam.class);
		if (param == null || StringUtils.isEmpty(param.value())) {
			return super.read(request, parameterDescriptor);
		}
		return super.read(request, parameterDescriptor.rename(param.value()));
	}

	@Override
	public boolean canWrite(HttpMessage message, TypeDescriptor typeDescriptor, Object value) {
		return false;
	}

	@Override
	public boolean canWrite(TypeDescriptor typeDescriptor, Object parameter) {
		return typeDescriptor.isAnnotationPresent(QueryParam.class);
	}

	@Override
	public UriComponentsBuilder write(UriComponentsBuilder builder, ParameterDescriptor parameterDescriptor,
			Object parameter) throws WebMessagelConverterException {
		QueryParam param = parameterDescriptor.getTypeDescriptor().getAnnotation(QueryParam.class);
		if (param == null || StringUtils.isEmpty(param.value())) {
			return super.write(builder, parameterDescriptor, parameter);
		}
		return super.write(builder, parameterDescriptor.rename(param.value()), parameter);
	}
}

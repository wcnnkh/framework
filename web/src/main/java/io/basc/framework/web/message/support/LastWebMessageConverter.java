package io.basc.framework.web.message.support;

import java.io.IOException;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.value.Value;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessagelConverterException;

/**
 * 应该排在最后一个
 * 
 * @author shuchaowen
 *
 */
class LastWebMessageConverter extends AbstractWebMessageConverter implements Ordered {

	@Override
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor) {
		return true;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return getConversionService().canConvert(TypeDescriptor.valueOf(String.class),
				new TypeDescriptor(parameterDescriptor))
				|| getConversionService().canConvert(TypeDescriptor.collection(List.class, String.class),
						new TypeDescriptor(parameterDescriptor));
	}

	protected Object readValue(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException {
		Object source;
		if (parameterDescriptor.getClass().isArray()) {
			Value[] values = WebUtils.getParameterValues(request, parameterDescriptor.getName());
			if (ArrayUtils.isEmpty(values)) {
				source = getDefaultValue(parameterDescriptor);
			} else {
				source = values;
			}
		} else {
			Value value = WebUtils.getParameter(request, parameterDescriptor.getName());
			if (value == null || value.isEmpty()) {
				source = getDefaultValue(parameterDescriptor);
			} else {
				source = value;
			}
		}
		return source;
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		Object source = readValue(parameterDescriptor, request);
		return getConversionService().convert(source, TypeDescriptor.forObject(source),
				new TypeDescriptor(parameterDescriptor));
	}

	@Override
	public void write(ServerHttpRequest request, ServerHttpResponse response, TypeDescriptor typeDescriptor,
			Object body) throws IOException, WebMessagelConverterException {
		if ((body instanceof String) || (ClassUtils.isPrimitiveOrWrapper(body.getClass()))) {
			response.setContentType(MimeTypeUtils.TEXT_HTML);
		} else {
			response.setContentType(MimeTypeUtils.APPLICATION_JSON);
		}

		String content = JSONUtils.getJsonSupport().toJSONString(body);
		response.getWriter().write(content);
	}

	@Override
	public Object read(ClientHttpResponse response, TypeDescriptor typeDescriptor)
			throws IOException, WebMessagelConverterException {
		return getMessageConverter().read(typeDescriptor, response);
	}
}

package io.basc.framework.web.message.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.utils.ArrayUtils;
import io.basc.framework.core.utils.ClassUtils;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.value.Value;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

import java.io.IOException;
import java.util.List;

public class ConversionMessageConverter implements WebMessageConverter {
	private final ConversionService conversionService;

	public ConversionMessageConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request) {
		return conversionService.canConvert(TypeDescriptor.valueOf(String.class),
				new TypeDescriptor(parameterDescriptor))
				|| conversionService.canConvert(TypeDescriptor.collection(List.class, String.class),
						new TypeDescriptor(parameterDescriptor));
	}

	@Override
	public Object read(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException {
		Object source;
		if (parameterDescriptor.getClass().isArray()) {
			Value[] values = WebUtils.getParameterValues(request, parameterDescriptor.getName());
			if(ArrayUtils.isEmpty(values)){
				Value defaultValue = parameterDescriptor.getDefaultValue();
				if(defaultValue != null){
					values = new Value[]{defaultValue};
				}
			}
			source = values;
		} else {
			Value value = WebUtils.getParameter(request, parameterDescriptor.getName());
			if(value == null || value.isEmpty()){
				Value defaultValue = parameterDescriptor.getDefaultValue();
				if(defaultValue != null){
					value = defaultValue;
				}
			}
			source = value;
		}
		return conversionService.convert(source, TypeDescriptor.forObject(source), new TypeDescriptor(parameterDescriptor));
	}

	@Override
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response) {
		return body != null;
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
		if ((body instanceof String) || (ClassUtils.isPrimitiveOrWrapper(body.getClass()))) {
			response.setContentType(MimeTypeUtils.TEXT_HTML);
		} else {
			response.setContentType(MimeTypeUtils.APPLICATION_JSON);
		}

		String content = JSONUtils.getJsonSupport().toJSONString(body);
		response.getWriter().write(content);
	}

}

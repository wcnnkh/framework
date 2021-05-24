package scw.web.message.support;

import java.io.IOException;
import java.util.List;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.core.utils.ClassUtils;
import scw.json.JSONUtils;
import scw.net.MimeTypeUtils;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.WebUtils;
import scw.web.message.WebMessageConverter;
import scw.web.message.WebMessagelConverterException;

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
		TypeDescriptor targetType = new TypeDescriptor(parameterDescriptor);
		TypeDescriptor sourceType = null;
		Object source;
		if (parameterDescriptor.getClass().isArray()) {
			source = WebUtils.getParameterValues(request, parameterDescriptor.getName());
		} else {
			source = WebUtils.getParameter(request, parameterDescriptor.getName());
		}
		
		if(source == null) {
			source = parameterDescriptor.getDefaultValue();
		}

		if (source != null) {
			sourceType = targetType.narrow(source);
		}

		return conversionService.convert(source, sourceType, targetType);
	}

	@Override
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request) {
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

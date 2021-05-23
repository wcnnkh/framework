package scw.web.convert.support;

import java.io.IOException;
import java.util.List;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.core.utils.ClassUtils;
import scw.net.MimeTypeUtils;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.WebUtils;
import scw.web.convert.WebMessageConverter;
import scw.web.convert.WebMessagelConverterException;

public class ConversionWebMessageConverters implements WebMessageConverter {
	private final ConversionService conversionService;

	public ConversionWebMessageConverters(ConversionService conversionService) {
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
			source = request.getParameterMap().getFirst(parameterDescriptor.getName());
		}

		if (source == null) {
			source = WebUtils.getRestfulParameter(request, parameterDescriptor.getName());
		}

		if (source != null) {
			sourceType = targetType.narrow(source);
		}
		return conversionService.convert(source, sourceType, targetType);
	}

	@Override
	public boolean canWrite(TypeDescriptor type, Object body) {
		if (body == null) {
			return false;
		}
		return conversionService.canConvert(TypeDescriptor.forObject(body), TypeDescriptor.valueOf(String.class));
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
		if ((body instanceof String) || (ClassUtils.isPrimitiveOrWrapper(body.getClass()))) {
			response.setContentType(MimeTypeUtils.TEXT_HTML);
		} else {
			response.setContentType(MimeTypeUtils.APPLICATION_JSON);
		}

		String content = (String) conversionService.convert(body, type.narrow(body),
				TypeDescriptor.valueOf(String.class));
		response.getWriter().write(content);
	}

}

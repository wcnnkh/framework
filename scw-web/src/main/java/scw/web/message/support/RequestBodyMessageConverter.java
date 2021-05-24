package scw.web.message.support;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.ResolvableType;
import scw.core.parameter.ParameterDescriptor;
import scw.dom.DomUtils;
import scw.json.JSONUtils;
import scw.util.MultiValueMap;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.WebUtils;
import scw.web.message.WebMessageConverter;
import scw.web.message.WebMessagelConverterException;
import scw.web.message.annotation.RequestBody;

public class RequestBodyMessageConverter implements WebMessageConverter {
	private static final TypeDescriptor PARAMETER_MAP_TYPE = TypeDescriptor.map(Map.class,
			ResolvableType.forClass(String.class), ResolvableType.forClassWithGenerics(List.class, String.class));

	private final ConversionService conversionService;

	public RequestBodyMessageConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request) {
		return parameterDescriptor.isAnnotationPresent(RequestBody.class);
	}

	@Override
	public Object read(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException {
		if (request.getHeaders().isJsonContentType()) {
			return JSONUtils.getJsonSupport().parseObject(request.getReader(), parameterDescriptor.getGenericType());
		} else if (request.getHeaders().isXmlContentType()) {
			Document document = DomUtils.getDomBuilder().parse(request.getReader());
			return conversionService.convert(document, TypeDescriptor.forObject(document),
					new TypeDescriptor(parameterDescriptor));
		} else {
			MultiValueMap<String, String> parameterMap = WebUtils.getParameterMap(request);
			return conversionService.convert(parameterMap, PARAMETER_MAP_TYPE, new TypeDescriptor(parameterDescriptor));
		}
	}

	@Override
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request) {
		return false;
	}

	@Override
	public void write(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
	}

}

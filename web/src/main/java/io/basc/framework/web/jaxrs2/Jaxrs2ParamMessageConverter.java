package io.basc.framework.web.jaxrs2;

import java.io.IOException;

import javax.ws.rs.FormParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.support.ConversionMessageConverter;

/**
 * 这是一个变种的实现，对于无论是PathParam,还是FormParam都会尝试从Path Form Body中获取值
 * 
 * @author shuchaowen
 *
 */
public class Jaxrs2ParamMessageConverter extends ConversionMessageConverter {

	public Jaxrs2ParamMessageConverter(ConversionService conversionService,
			ParameterFactory defaultValueFactory) {
		super(conversionService, defaultValueFactory);
	}

	@Override
	public boolean canRead(ParameterDescriptor parameterDescriptor, ServerHttpRequest request) {
		return (parameterDescriptor.isAnnotationPresent(FormParam.class)
				|| parameterDescriptor.isAnnotationPresent(PathParam.class)
				|| parameterDescriptor.isAnnotationPresent(QueryParam.class)
				|| parameterDescriptor.isAnnotationPresent(MatrixParam.class))
				&& super.canRead(parameterDescriptor, request);
	}

	@Override
	public Object read(ParameterDescriptor parameterDescriptor, ServerHttpRequest request)
			throws IOException, WebMessagelConverterException {
		FormParam formParam = parameterDescriptor.getAnnotation(FormParam.class);
		if (formParam != null && StringUtils.isNotEmpty(formParam.value())) {
			return super.read(parameterDescriptor.rename(formParam.value()), request);
		}

		PathParam pathParam = parameterDescriptor.getAnnotation(PathParam.class);
		if (pathParam != null && StringUtils.isNotEmpty(pathParam.value())) {
			return super.read(parameterDescriptor.rename(pathParam.value()), request);
		}

		QueryParam queryParam = parameterDescriptor.getAnnotation(QueryParam.class);
		if (queryParam != null && StringUtils.isNotEmpty(queryParam.value())) {
			return super.read(parameterDescriptor.rename(queryParam.value()), request);
		}
		
		MatrixParam matrixParam = parameterDescriptor.getAnnotation(MatrixParam.class);
		if (matrixParam != null && StringUtils.isNotEmpty(matrixParam.value())) {
			return super.read(parameterDescriptor.rename(matrixParam.value()), request);
		}
		return super.read(parameterDescriptor, request);
	}

	@Override
	public boolean canWrite(TypeDescriptor type, Object body, ServerHttpRequest request, ServerHttpResponse response) {
		return false;
	}
}

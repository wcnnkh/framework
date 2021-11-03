package io.basc.framework.mvc.jaxrs2;

import java.io.IOException;

import javax.ws.rs.FormParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.http.HttpMessage;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.support.ConversionMessageConverter;

/**
 * 这是一个变种的实现，对于无论是PathParam,还是FormParam都会尝试从Path Form Body中获取值
 * 
 * @author shuchaowen
 *
 */
public class Jaxrs2ParamMessageConverter extends ConversionMessageConverter {

	public Jaxrs2ParamMessageConverter(ConversionService conversionService, ParameterFactory defaultValueFactory) {
		super(conversionService, defaultValueFactory);
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return (parameterDescriptor.isAnnotationPresent(FormParam.class)
				|| parameterDescriptor.isAnnotationPresent(PathParam.class)
				|| parameterDescriptor.isAnnotationPresent(QueryParam.class)
				|| parameterDescriptor.isAnnotationPresent(MatrixParam.class)) && super.isAccept(parameterDescriptor);
	}

	@Override
	public Object read(ServerHttpRequest request, ParameterDescriptor parameterDescriptor)
			throws IOException, WebMessagelConverterException {
		FormParam formParam = parameterDescriptor.getAnnotation(FormParam.class);
		if (formParam != null && StringUtils.isNotEmpty(formParam.value())) {
			return super.read(request, parameterDescriptor.rename(formParam.value()));
		}

		PathParam pathParam = parameterDescriptor.getAnnotation(PathParam.class);
		if (pathParam != null && StringUtils.isNotEmpty(pathParam.value())) {
			return super.read(request, parameterDescriptor.rename(pathParam.value()));
		}

		QueryParam queryParam = parameterDescriptor.getAnnotation(QueryParam.class);
		if (queryParam != null && StringUtils.isNotEmpty(queryParam.value())) {
			return super.read(request, parameterDescriptor.rename(queryParam.value()));
		}

		MatrixParam matrixParam = parameterDescriptor.getAnnotation(MatrixParam.class);
		if (matrixParam != null && StringUtils.isNotEmpty(matrixParam.value())) {
			return super.read(request, parameterDescriptor.rename(matrixParam.value()));
		}
		return super.read(request, parameterDescriptor);
	}

	@Override
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor) {
		return false;
	}
}

package scw.servlet.parameter;

import scw.core.utils.StringUtils;
import scw.mvc.ParameterDefinition;
import scw.mvc.annotation.Parameter;
import scw.servlet.ParameterFilter;
import scw.servlet.ParameterFilterChain;
import scw.servlet.Request;
import scw.servlet.ServletUtils;

public final class LastParameterFilter implements ParameterFilter {

	public Object filter(Request request,
			ParameterDefinition parameterDefinition, ParameterFilterChain chain)
			throws Exception {
		String name = parameterDefinition.getName();
		Parameter parameter = parameterDefinition
				.getAnnotation(Parameter.class);
		if (parameter != null) {
			name = parameter.value();
		}

		if (parameterDefinition.getType().isArray()) {
			return request.getArray(name, parameterDefinition.getType()
					.getComponentType());
		} else {
			Object value = request.getBean(parameterDefinition.getType());
			if (value == null) {
				value = StringUtils.isEmpty(name) ? request
						.getObject(parameterDefinition.getType()) : request
						.getObject(name, parameterDefinition.getType());
			}

			if (value == null) {
				value = ServletUtils.getRequestObjectParameterWrapper(request,
						parameterDefinition.getType(), name);
			}

			if (value == null) {
				value = chain.doFilter(request, parameterDefinition);
			}

			return value;
		}
	}
}

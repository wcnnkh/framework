package scw.servlet.parameter;

import scw.servlet.ParameterDefinition;
import scw.servlet.ParameterFilter;
import scw.servlet.ParameterFilterChain;
import scw.servlet.Request;
import scw.servlet.ServletUtils;

public final class LastParameterFilter implements ParameterFilter {
	private boolean last;

	public LastParameterFilter() {
		this(true);
	}

	public LastParameterFilter(boolean last) {
		this.last = last;
	}

	public Object filter(Request request, ParameterDefinition parameterDefinition, ParameterFilterChain chain)
			throws Exception {
		if (parameterDefinition.getType().isArray()) {
			return request.getArray(parameterDefinition.getName(), parameterDefinition.getType().getComponentType());
		} else {
			Object value = request.getObject(parameterDefinition.getName(), parameterDefinition.getType());
			if (value == null && last) {
				return ServletUtils.getRequestObjectParameterWrapper(request, parameterDefinition.getType(),
						parameterDefinition.getName());
			}
			return value == null ? chain.doFilter(request, parameterDefinition) : value;
		}
	}
}

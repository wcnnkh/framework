package scw.servlet;

import scw.mvc.ParameterDefinition;

public interface ParameterFilterChain {
	Object doFilter(Request request, ParameterDefinition parameterDefinition) throws Exception;
}

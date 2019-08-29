package scw.servlet;

import scw.mvc.ParameterDefinition;

public interface ParameterFilter {
	Object filter(Request request, ParameterDefinition parameterDefinition, ParameterFilterChain chain)
			throws Exception;
}

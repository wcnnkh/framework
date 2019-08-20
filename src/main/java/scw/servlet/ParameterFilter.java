package scw.servlet;

public interface ParameterFilter {
	Object filter(Request request, ParameterDefinition parameterDefinition, ParameterFilterChain chain)
			throws Exception;
}

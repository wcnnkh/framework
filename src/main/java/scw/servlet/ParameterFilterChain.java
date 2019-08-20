package scw.servlet;

public interface ParameterFilterChain {
	Object doFilter(Request request, ParameterDefinition parameterDefinition) throws Exception;
}

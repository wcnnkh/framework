package scw.mvc;

public interface ParameterFilterChain {
	Object doFilter(Channel channel, ParameterDefinition parameterDefinition) throws Throwable;
}
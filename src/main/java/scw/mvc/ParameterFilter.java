package scw.mvc;

public interface ParameterFilter {
	Object filter(Channel channel, ParameterDefinition parameterDefinition, ParameterFilterChain chain)
			throws Throwable;
}

package scw.core.parameter;

public interface ParameterFactory<T extends ParameterDescriptor> {
	Object getParameter(T parameterDescriptor);
}

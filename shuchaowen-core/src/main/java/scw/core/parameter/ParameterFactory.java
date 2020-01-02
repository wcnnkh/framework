package scw.core.parameter;

public interface ParameterFactory<T extends ParameterConfig> {
	Object getParameter(T parameterConfig);
}

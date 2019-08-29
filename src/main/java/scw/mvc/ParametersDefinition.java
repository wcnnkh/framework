package scw.mvc;

public interface ParametersDefinition {
	int getParameterCount();

	ParameterDefinition getParameterDefinition(int index);
}
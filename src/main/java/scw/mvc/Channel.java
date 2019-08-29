package scw.mvc;

public interface Channel {
	Object getParameter(ParameterDefinition parameterDefinition);

	void write(Object obj) throws Throwable;
}

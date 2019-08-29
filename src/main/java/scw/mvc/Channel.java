package scw.mvc;

public interface Channel {
	long getCreateTime();
	
	Object getParameter(ParameterDefinition parameterDefinition);

	void write(Object obj) throws Throwable;
}

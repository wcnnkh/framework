package scw.core.parameter;

import scw.util.Accept;

public interface ParameterFactory extends Accept<ParameterDescriptor> {
	boolean accept(ParameterDescriptor parameterDescriptor);
	
	Object getParameter(ParameterDescriptor parameterDescriptor);
}

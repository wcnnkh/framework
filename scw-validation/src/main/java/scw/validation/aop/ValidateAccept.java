package scw.validation.aop;

import scw.core.parameter.ParameterDescriptor;
import scw.util.Accept;

public interface ValidateAccept extends Accept<ParameterDescriptor> {
	boolean accept(ParameterDescriptor parameterDescriptor);
}

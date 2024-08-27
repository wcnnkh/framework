package io.basc.framework.execution.param;

import java.io.Serializable;
import java.util.Arrays;

import io.basc.framework.util.Elements;

public class Args extends SimpleParameters implements Serializable {
	private static final long serialVersionUID = 1L;

	public Args(Parameter... parameters) {
		this(Arrays.asList(parameters));
	}

	public Args(Iterable<? extends Parameter> parameters) {
		setElements(Elements.of(parameters));
	}

	public Args(Iterable<? extends ParameterDescriptor> parameterDescriptors, Iterable<? extends Object> args) {
		this(Elements.of(parameterDescriptors).parallel(Elements.of(args)).map((e) -> {
			SimpleParameter parameter = new SimpleParameter(e.getLeftValue());
			parameter.setValue(e.getRightValue());
			return parameter;
		}));
	}
}

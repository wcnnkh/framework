package io.basc.framework.execution.param;

import java.io.Serializable;
import java.util.Arrays;

import io.basc.framework.util.element.Elements;

public class Args extends SimpleParameters implements Serializable {
	private static final long serialVersionUID = 1L;

	public Args(Parameter... parameters) {
		this(Arrays.asList(parameters));
	}

	public Args(Iterable<Parameter> parameters) {
		setElements(Elements.of(parameters));
	}
}

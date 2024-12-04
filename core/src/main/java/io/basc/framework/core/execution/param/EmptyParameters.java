package io.basc.framework.core.execution.param;

import java.io.Serializable;

import io.basc.framework.core.convert.transform.Parameter;
import io.basc.framework.util.Elements;

public class EmptyParameters implements Parameters, Serializable {
	private static final long serialVersionUID = 1L;

	public static final EmptyParameters INSTANCE = new EmptyParameters();

	@Override
	public Elements<Parameter> getElements() {
		return Elements.empty();
	}
}

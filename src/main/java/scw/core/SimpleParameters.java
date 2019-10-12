package scw.core;

import java.io.Serializable;

public final class SimpleParameters implements Parameters, Serializable {
	private static final long serialVersionUID = 1L;
	private Object[] parameters;

	public SimpleParameters(Object... parameters) {
		this.parameters = parameters;
	}

	public Object[] getParameters() {
		return parameters;
	}

}

package scw.core;

import java.io.Serializable;

public final class DefaultParameters implements Parameters, Serializable {
	private static final long serialVersionUID = 1L;
	private Object[] parameters;

	protected DefaultParameters() {
	}

	public DefaultParameters(Object... parameters) {
		this.parameters = parameters;
	}

	public Object[] getParameters() {
		return parameters;
	}

}

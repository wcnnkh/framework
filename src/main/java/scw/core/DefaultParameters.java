package scw.core;

import java.io.Serializable;

import scw.core.utils.ArrayUtils;

public final class DefaultParameters implements Parameters, Serializable {
	private static final long serialVersionUID = 1L;
	private Object[] parameters;

	protected DefaultParameters() {
	}

	public DefaultParameters(Object... parameters) {
		this.parameters = parameters;
	}

	public DefaultParameters(Parameter[] parameters) {
		if (ArrayUtils.isEmpty(parameters)) {
			this.parameters = new Object[0];
		}

		this.parameters = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			if (parameter == null) {
				continue;
			}

			this.parameters[i] = parameter.getParameter();
		}
	}

	public Object[] getParameters() {
		return parameters;
	}

}

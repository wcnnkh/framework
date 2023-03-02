package io.basc.framework.mvc.action;

import java.io.Serializable;
import java.util.Optional;

import io.basc.framework.mvc.HttpChannel;

public class ActionParameters implements Serializable {
	private static final long serialVersionUID = 1L;
	private Optional<Object[]> parameters = Optional.empty();

	public ActionParameters() {
	}

	public ActionParameters(ActionParameters parameters) {
		this.parameters = parameters.parameters;
	}

	public ActionParameters(Object[] parameters) {
		this.parameters = Optional.of(parameters);
	}

	public Optional<Object[]> getParameters() {
		return parameters;
	}

	public boolean isPresent() {
		return parameters.isPresent();
	}

	public Object[] getParameters(HttpChannel httpChannel, Action action) {
		if (!parameters.isPresent()) {
			parameters = Optional.of(httpChannel.getParameters(action.getParameterDescriptors()));
		}
		return parameters.get();
	}

	public void setParameters(Object[] parameters) {
		this.parameters = Optional.of(parameters);
	}

	public void clear() {
		parameters = Optional.empty();
	}
}

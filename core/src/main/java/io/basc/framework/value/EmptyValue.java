package io.basc.framework.value;

import java.io.Serializable;

public class EmptyValue implements Value, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Object getSource() {
		return null;
	}

	@Override
	public Value orElse(Value other) {
		return other;
	}
}

package io.basc.framework.core.convert.lang;

import java.io.Serializable;

import io.basc.framework.core.convert.ValueWrapper;

public class EmptyValue implements ValueWrapper, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Object getSource() {
		return null;
	}
}

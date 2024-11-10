package io.basc.framework.convert.lang;

import java.io.Serializable;

public class EmptyValue implements ValueWrapper, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Object getSource() {
		return null;
	}
}

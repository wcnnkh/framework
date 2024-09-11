package io.basc.framework.convert.lang;

import java.io.Serializable;

public class EmptyValue implements Value, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Object getSource() {
		return null;
	}
}

package io.basc.framework.messageing;

import java.util.HashMap;

import io.basc.framework.core.convert.Any;

public class MessageHeaders extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	@Override
	public Any get(Object key) {
		Object value = super.get(key);
		if (value == null) {
			return Any.EMPTY;
		}
		return Any.of(value);
	}
}

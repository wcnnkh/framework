package io.basc.framework.messageing;

import java.util.HashMap;

import io.basc.framework.core.convert.ValueWrapper;

public class MessageHeaders extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	@Override
	public ValueWrapper get(Object key) {
		Object value = super.get(key);
		if (value == null) {
			return ValueWrapper.EMPTY;
		}
		return ValueWrapper.of(value);
	}
}

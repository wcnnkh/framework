package io.basc.framework.messageing;

import java.util.HashMap;

import io.basc.framework.value.Value;

public class MessageHeaders extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	@Override
	public Value get(Object key) {
		Object value = super.get(key);
		if (value == null) {
			return Value.EMPTY;
		}
		return Value.of(value);
	}
}

package io.basc.framework.messageing;

import java.util.HashMap;

import io.basc.framework.convert.lang.ObjectValue;

public class MessageHeaders extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	@Override
	public ObjectValue get(Object key) {
		Object value = super.get(key);
		if (value == null) {
			return ObjectValue.EMPTY;
		}
		return ObjectValue.of(value);
	}
}

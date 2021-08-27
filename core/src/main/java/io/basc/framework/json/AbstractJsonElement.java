package io.basc.framework.json;

import io.basc.framework.value.AbstractValue;
import io.basc.framework.value.Value;

public abstract class AbstractJsonElement extends AbstractValue implements
		JsonElement {
	private static final long serialVersionUID = 1L;

	public AbstractJsonElement(Value defaultValue) {
		super(defaultValue);
	}

	@Override
	public String toString() {
		return toJSONString();
	}
}

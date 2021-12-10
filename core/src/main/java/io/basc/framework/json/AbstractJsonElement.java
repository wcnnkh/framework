package io.basc.framework.json;

import io.basc.framework.value.AbstractValue;

public abstract class AbstractJsonElement extends AbstractValue implements JsonElement {

	@Override
	public String toString() {
		return toJSONString();
	}
}

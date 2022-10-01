package io.basc.framework.json;

import io.basc.framework.value.DirectValue;

public abstract class AbstractJsonElement extends DirectValue implements JsonElement {

	@Override
	public String toString() {
		return toJSONString();
	}
}

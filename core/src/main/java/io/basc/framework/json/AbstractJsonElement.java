package io.basc.framework.json;

public abstract class AbstractJsonElement implements JsonElement {

	@Override
	public String toString() {
		return toJsonString();
	}
}

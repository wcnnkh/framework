package scw.json;

import scw.util.AbstractValue;

public abstract class AbstractJsonElement extends AbstractValue implements JsonElement {

	@Override
	public String toString() {
		return getAsString();
	}
}

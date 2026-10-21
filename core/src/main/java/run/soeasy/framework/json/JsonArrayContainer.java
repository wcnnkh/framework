package run.soeasy.framework.json;

import java.util.ArrayList;
import java.util.Collection;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.CollectionContainer;

public class JsonArrayContainer extends CollectionContainer<JsonElement, Collection<JsonElement>> implements JsonArray {

	public JsonArrayContainer() {
		this(new ArrayList<>());
	}

	public JsonArrayContainer(@NonNull Collection<JsonElement> container) {
		super(container);
	}
}

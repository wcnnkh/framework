package run.soeasy.framework.json;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.MapContainer;

public class JsonObjectContainer extends MapContainer<String, JsonElement, Map<String, JsonElement>>
		implements JsonObject {

	public JsonObjectContainer() {
		this(new LinkedHashMap<>());
	}

	public JsonObjectContainer(@NonNull Map<String, JsonElement> container) {
		super(container);
	}

}

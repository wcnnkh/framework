package scw.value.event;

import scw.compatible.map.CompatibleMap;
import scw.event.NamedEventDispatcher;
import scw.value.Value;

public interface DynamicMap extends CompatibleMap<String, Value> {
	NamedEventDispatcher<ValueEvent> getEventDispatcher();
}
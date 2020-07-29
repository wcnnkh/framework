package scw.value.event;

import scw.compatible.map.CompatibleMap;
import scw.event.NamedEventDispatcher;
import scw.value.Value;

public interface DynamicMap extends CompatibleMap<String, Value> {
	NamedEventDispatcher<ValueEvent> getEventDispatcher();
	
	DynamicMapRegistration loadProperties(String resource, ValueCreator creator);
	
	DynamicMapRegistration loadProperties(String resource, String charsetName, ValueCreator creator);
	
	DynamicMapRegistration loadProperties(String keyPrefix, String resource, String charsetName,
			ValueCreator creator);
}
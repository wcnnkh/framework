package scw.json;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;
import scw.event.support.DefaultEventDispatcher;
import scw.value.property.BasePropertyFactory;
import scw.value.property.PropertyEvent;

public abstract class JsonObject extends AbstractJson<String> implements BasePropertyFactory {
	private NamedEventDispatcher<PropertyEvent> dispatcher;
	
	public abstract void put(String key, Object value);
	
	
	public Enumeration<String> enumerationKeys() {
		return Collections.enumeration(keys());
	}

	public abstract Collection<String> keys();

	public abstract boolean containsKey(String key);
	
	public boolean isSupportListener(String key) {
		return containsKey(key);
	}
	
	public void publishEvent(String name, PropertyEvent event) {
		if(dispatcher == null){
			return ;
		}

		dispatcher.publishEvent(name, event);
	}
	
	public void unregister(String name) {
		if(dispatcher == null){
			return ;
		}
		
		dispatcher.unregister(name);
	}
	
	public EventRegistration registerListener(String key,
			EventListener<PropertyEvent> eventListener) {
		if(dispatcher == null){
			dispatcher = new DefaultEventDispatcher<PropertyEvent>(false);
		}
		
		return dispatcher.registerListener(key, eventListener);
	}
}

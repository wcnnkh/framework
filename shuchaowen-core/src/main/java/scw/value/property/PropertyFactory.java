package scw.value.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import scw.core.utils.CollectionUtils;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.support.EmptyEventRegistration;
import scw.util.MultiEnumeration;
import scw.value.StringValueFactory;
import scw.value.Value;

public class PropertyFactory extends StringValueFactory implements
		BasePropertyFactory {
	private List<BasePropertyFactory> basePropertyFactories;

	public void addBasePropertyFactory(BasePropertyFactory basePropertyFactory) {
		if (basePropertyFactory == null) {
			return;
		}

		if (basePropertyFactories == null) {
			basePropertyFactories = new ArrayList<BasePropertyFactory>();
		}
		basePropertyFactories.add(basePropertyFactory);
	}

	public void addBasePropertyFactory(
			List<BasePropertyFactory> basePropertyFactories) {
		if (CollectionUtils.isEmpty(basePropertyFactories)) {
			return;
		}

		for (BasePropertyFactory propertyFactory : basePropertyFactories) {
			addBasePropertyFactory(propertyFactory);
		}
	}

	@Override
	public Value get(String key) {
		if (basePropertyFactories != null) {
			for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
				Value value = basePropertyFactory.get(key);
				if (value != null) {
					return value;
				}
			}
		}
		return super.get(key);
	}

	public Enumeration<String> enumerationKeys() {
		if (basePropertyFactories == null) {
			return Collections.emptyEnumeration();
		}

		List<Enumeration<String>> enumerations = new LinkedList<Enumeration<String>>();
		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			enumerations.add(basePropertyFactory.enumerationKeys());
		}
		return new MultiEnumeration<String>(enumerations);
	}

	public boolean containsKey(String key) {
		if (basePropertyFactories == null) {
			return false;
		}

		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			if (basePropertyFactory.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	public EventRegistration registerListener(String key,
			EventListener<PropertyEvent> eventListener) {
		if (basePropertyFactories == null) {
			return new EmptyEventRegistration();
		}

		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			if (basePropertyFactory.containsKey(key)) {
				return basePropertyFactory.registerListener(key, eventListener);
			}
		}
		return new EmptyEventRegistration();
	}

	public void unregister(String name) {
		if(basePropertyFactories == null){
			return ;
		}
		
		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			if (basePropertyFactory.containsKey(name)) {
				basePropertyFactory.unregister(name);
			}
		}
	}

	public void publishEvent(String name, PropertyEvent event) {
		if(basePropertyFactories == null){
			return ;
		}
		
		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			if (basePropertyFactory.containsKey(name)) {
				basePropertyFactory.publishEvent(name, event);
			}
		}
	}
}

package scw.value.property;

import java.util.Enumeration;

import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.value.BaseValueFactory;

public interface BasePropertyFactory extends BaseValueFactory<String>{
	/**
	 * 能获取到值不一定代表可以通过此方法枚举到key
	 * @return
	 */
	Enumeration<String> enumerationKeys();
	
	boolean containsKey(String key);
	
	EventRegistration registerListener(String key, EventListener<PropertyEvent> eventListener);
}

package scw.value.property;

import java.util.Enumeration;

import scw.event.NamedEventDispatcher;
import scw.value.BaseValueFactory;

public interface BasePropertyFactory extends BaseValueFactory<String>, NamedEventDispatcher<PropertyEvent>{
	Enumeration<String> enumerationKeys();
	
	boolean containsKey(String key);
}

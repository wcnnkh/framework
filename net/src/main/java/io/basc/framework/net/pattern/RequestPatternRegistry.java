package io.basc.framework.net.pattern;

import java.util.Map.Entry;

import io.basc.framework.observe.register.ElementRegistration;
import io.basc.framework.observe.register.EntryRegistry;

public class RequestPatternRegistry<V> extends EntryRegistry<RequestPattern, V> {
	
	public ElementRegistration<Entry<RequestPattern, V>> register(RequestPatternCapable keyCapable, V value) {
		RequestPattern key = keyCapable.getRequestPattern();
		return register(key, value);
	}

}

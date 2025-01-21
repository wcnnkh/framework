package io.basc.framework.net.pattern;

import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.register.container.DefaultMapContainer;

public class RequestPatternRegistry<V> extends DefaultMapContainer<RequestPattern, V> {

	public Registration register(RequestPatternCapable keyCapable, V value) {
		RequestPattern key = keyCapable.getRequestPattern();
		return register(key, value);
	}

}

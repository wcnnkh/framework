package io.basc.framework.net.pattern;

import java.util.Map;

import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.function.Supplier;
import io.basc.framework.util.register.container.DefaultMapContainer;
import io.basc.framework.util.register.container.EntryRegistration;
import lombok.NonNull;

public class RequestPatternRegistry<V> extends DefaultMapContainer<RequestPattern, V> {

	public RequestPatternRegistry(
			@NonNull Supplier<? extends Map<RequestPattern, EntryRegistration<RequestPattern, V>>, ? extends RuntimeException> containerSource) {
		super(containerSource);
	}

	public Registration register(RequestPatternCapable keyCapable, V value) {
		RequestPattern key = keyCapable.getRequestPattern();
		return register(key, value);
	}

}

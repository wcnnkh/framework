package io.basc.framework.observe.properties;

import io.basc.framework.transform.factory.PropertyFactory;
import io.basc.framework.util.Elements;
import io.basc.framework.util.actor.EventListener;
import io.basc.framework.util.actor.EventRegistrationException;
import io.basc.framework.util.register.Registration;

public interface ObservablePropertyFactory extends ObservableValueFactory<String>, PropertyFactory {

	default Registration registerKeysListener(EventListener<Elements<String>> eventListener)
			throws EventRegistrationException {
		return registerBatchListener((events) -> events.map((e) -> e.getKey()));
	}
}

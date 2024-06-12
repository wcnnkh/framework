package io.basc.framework.observe.properties;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.transform.factory.PropertyFactory;
import io.basc.framework.util.Registration;
import io.basc.framework.util.element.Elements;

public interface ObservablePropertyFactory extends ObservableValueFactory<String>, PropertyFactory {

	default Registration registerKeysListener(EventListener<Elements<String>> eventListener)
			throws EventRegistrationException {
		return registerBatchListener((events) -> events.map((e) -> e.getKey()));
	}
}

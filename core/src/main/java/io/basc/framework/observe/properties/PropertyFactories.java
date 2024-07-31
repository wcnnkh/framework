package io.basc.framework.observe.properties;

import io.basc.framework.convert.lang.Value;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.observe.Observer;
import io.basc.framework.observe.register.ElementRegistration;
import io.basc.framework.transform.factory.PropertyFactory;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.RegistrationException;

/**
 * 频繁修改属性建议使用此类, 不会触发数据收集
 * 
 * @author wcnnkh
 *
 */
public class PropertyFactories extends ValueFactories<String, PropertyFactory> implements PropertyFactory {

	public PropertyFactories() {
		setServiceClass(PropertyFactory.class);
	}

	@Override
	public boolean containsKey(String key) {
		for (PropertyFactory factory : getServices()) {
			if (factory == null || factory == this) {
				continue;
			}

			if (factory.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Elements<String> keys() {
		return getServices().flatMap((e) -> e.keys()).distinct();
	}

	@Override
	protected ElementRegistration<PropertyFactory> createElementRegistration(PropertyFactory element) {
		ElementRegistration<PropertyFactory> registration = super.createElementRegistration(element);
		
	}

	@Override
	public Registration register(PropertyFactory element, int weight) throws RegistrationException {
		Registration registration = super.register(element, weight);
		if (element instanceof ObservablePropertyFactory) {
			registration = registration.and(() -> ((ObservablePropertyFactory) element)
					.registerBatchListener(propertyObserver::publishBatchEvent));
		}
		return registration;
	}

	private volatile Registration registration;

	private void refreshPropertyObserver() {
		if (propertyObserver.getListenerCount() > 0) {
			if (registration == null) {
				synchronized (this) {
					if (registration == null) {
						registration = registerBatchListener((events) -> {
							Elements<PropertyChangeEvent<String, Value>> propertyChangeEvents = events
									.flatMap((event) -> event.getPayload().keys()
											.map((key) -> new PropertyChangeEvent<>(event.getSource(), event.getType(),
													key, event.getPayload().get(key))));
							propertyObserver.publishBatchEvent(propertyChangeEvents);
						});
					}
				}
			}
		} else {
			if (registration != null) {
				synchronized (this) {
					if (registration != null) {
						registration.unregister();
						registration = null;
					}
				}
			}
		}
	}

	private final Observer<PropertyChangeEvent<String, Value>> propertyObserver = new Observer<>();

	public Registration registerPropertyListener(BatchEventListener<PropertyChangeEvent<String, Value>> eventListener) {
		Registration registration = propertyObserver.registerBatchListener(eventListener);
		refreshPropertyObserver();
		return registration.and(() -> reload());
	}
}

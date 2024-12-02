package io.basc.framework.observe.properties;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

import io.basc.framework.core.convert.ValueWrapper;
import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.DefaultPropertiesResolver;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.Constants;
import io.basc.framework.observe.value.ObservableValue;
import io.basc.framework.observe.watch.ResourceObserver;
import io.basc.framework.transform.factory.config.EditablePropertyFactory;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ConvertibleIterator;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.EventRegistrationException;
import io.basc.framework.util.actor.batch.BatchEventListener;
import io.basc.framework.util.observe_old.Observer;
import io.basc.framework.util.register.Registration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObservableProperties extends Observer<PropertyChangeEvent<String, ValueWrapper>>
		implements ObservablePropertyFactory, EditablePropertyFactory {
	private Charset charset = Constants.UTF_8;
	private final Properties properties;
	private PropertiesResolver propertiesResolver = DefaultPropertiesResolver.getInstance();
	private final PropertyWrapper propertyWrapper;

	public ObservableProperties() {
		this(PropertyWrapper.CREATOR);
	}

	public ObservableProperties(Properties properties, PropertyWrapper propertyWrapper) {
		this.properties = properties;
		this.propertyWrapper = propertyWrapper;
	}

	public ObservableProperties(PropertyWrapper propertyWrapper) {
		this(new Properties(), propertyWrapper);
	}

	public Registration bind(Resource resource, ResourceObserver resourceWatcher) {
		load(resource);
		Registration registration = resourceWatcher.register(resource);
		registration = registration.and(resourceWatcher.registerBatchListener((events) -> {
			if (events.map((e) -> e.getPayload().getResource()).contains(resource)) {
				load(resource);
			}
		}));
		return registration;
	}

	private ValueWrapper convert(String key, Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof ValueWrapper) {
			return (ValueWrapper) value;
		}

		return propertyWrapper.wrap(key, value);
	}

	@Override
	public ValueWrapper get(String key) {
		Object value = properties.get(key);
		return convert(key, value);
	}

	public Properties getProperties() {
		return properties;
	}

	private class ObservablePropertiesToObservableValue implements ObservableValue<Properties> {

		@Override
		public Properties orElse(Properties other) {
			return (properties == null || properties.isEmpty()) ? other : properties;
		}

		@Override
		public Registration registerBatchListener(BatchEventListener<ChangeEvent> batchEventListener)
				throws EventRegistrationException {
			return ObservableProperties.this
					.registerBatchListener((events) -> batchEventListener.onEvent(events.map((e) -> e)));
		}
	}

	public ObservableValue<Properties> asObservableValue() {
		return new ObservablePropertiesToObservableValue();
	}

	@Override
	public Elements<String> keys() {
		return Elements.of(() -> new ConvertibleIterator<Object, String>(CollectionUtils.toIterator(properties.keys()),
				ObjectUtils::toString));
	}

	public void load(Resource resource) {
		Properties properties = new Properties();
		propertiesResolver.resolveProperties(properties, resource, charset);
		setProperties(properties);
	}

	public void setProperties(Map<?, ?> properties) {
		// TODO 待优化,不应该触发所有key的变更
		properties.clear();
		this.properties.putAll(properties);
	}

	private void load(ObservableValue<? extends Map<?, ?>> observableValue) {
		Properties properties = new Properties();
		observableValue.ifPresent((map) -> properties.putAll(properties));
		setProperties(properties);
	}

	public Registration bind(ObservableValue<? extends Map<?, ?>> observableValue) {
		load(observableValue);
		return observableValue.registerBatchListener((es) -> {
			load(observableValue);
		});
	}

	@Override
	public ValueWrapper put(String key, Object value) {
		Object oldValue = put(key, value);
		return convert(key, oldValue);
	}

	@Override
	public ValueWrapper put(String key, ValueWrapper value) {
		Object oldValue = properties.put(key, value);
		return convert(key, oldValue);
	}

	@Override
	public ValueWrapper remove(String key) {
		Object value = properties.remove(key);
		return convert(key, value);
	}
}

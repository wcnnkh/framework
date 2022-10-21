package io.basc.framework.value.support;

import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;
import io.basc.framework.value.ConfigurablePropertyFactory;
import io.basc.framework.value.PropertyFactories;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.PropertyWrapper;
import io.basc.framework.value.Value;

public class DefaultPropertyFactory extends DefaultValueFactory<String, PropertyFactory>
		implements ConfigurablePropertyFactory, Configurable {
	private final PropertyWrapper propertyWrapper;
	private final PropertyFactories propertyFactories = new PropertyFactories();
	private final ObservablePropertyFactory observable;

	public DefaultPropertyFactory() {
		this(null);
	}

	public DefaultPropertyFactory(@Nullable PropertyWrapper propertyWrapper) {
		this.propertyWrapper = propertyWrapper;
		this.observable = new ObservablePropertyFactory(propertyWrapper);
	}

	public Iterator<String> iterator() {
		return stream().iterator();
	}

	public ObservablePropertyFactory getObservable() {
		return observable;
	}

	@Override
	public boolean isConfigured() {
		return propertyFactories.isConfigured();
	}

	public PropertyFactories getPropertyFactories() {
		return propertyFactories;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		propertyFactories.configure(serviceLoaderFactory);
	}

	@Override
	public Stream<String> stream() {
		Stream<String> stream = super.keySet().stream();
		stream = Stream.concat(stream, propertyFactories.stream());
		return Stream.concat(stream, observable.stream()).distinct();
	}

	@Override
	public Value get(String key) {
		Value value = super.get(key);
		if (value != null && value.isPresent()) {
			return value;
		}

		value = propertyFactories.get(key);
		if (value != null && value.isPresent()) {
			return value;
		}

		value = observable.get(key);
		return value != null && value.isPresent() ? value : Value.EMPTY;
	}

	@Override
	public boolean containsKey(String key) {
		return super.containsKey(key) || propertyFactories.containsKey(key) || observable.containsKey(key);
	}

	@Override
	public Registration registerListener(String name, EventListener<ChangeEvent<String>> eventListener) {
		return super.registerListener(name, eventListener).and(propertyFactories.registerListener(name, eventListener))
				.and(observable.registerListener(name, eventListener));
	}

	public void put(String key, Object value) {
		if (propertyWrapper == null) {
			super.put(key, value);
			return;
		}

		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		super.put(key, propertyWrapper.wrap(key, value));
	}

	public boolean putIfAbsent(String key, Object value) {
		if (propertyWrapper == null) {
			return super.putIfAbsent(key, value);
		}
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return super.putIfAbsent(key, propertyWrapper.wrap(key, value));
	}
}

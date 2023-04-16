package io.basc.framework.value.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.event.BroadcastEventRegistry;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.MultiElements;
import io.basc.framework.value.ConfigurablePropertyFactory;
import io.basc.framework.value.PropertyFactories;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.PropertyWrapper;
import io.basc.framework.value.Value;

public class DefaultPropertyFactory extends DefaultValueFactory<String, PropertyFactory>
		implements ConfigurablePropertyFactory, Configurable {
	private static final Function<Properties, Map<String, Value>> CONVERTER = (properties) -> {
		if (CollectionUtils.isEmpty(properties)) {
			return Collections.emptyMap();
		}

		Map<String, Value> map = new LinkedHashMap<>(properties.size());
		for (Entry<?, ?> entry : properties.entrySet()) {
			map.put(String.valueOf(entry.getKey()), Value.of(entry.getValue()));
		}
		return map;
	};

	private final PropertyWrapper propertyWrapper;
	private final PropertyFactories propertyFactories = new PropertyFactories();
	private final ObservablePropertyFactory observable;

	public DefaultPropertyFactory() {
		this(null);
	}

	public DefaultPropertyFactory(@Nullable PropertyWrapper propertyWrapper) {
		super(CONVERTER);
		this.propertyWrapper = propertyWrapper;
		this.observable = new ObservablePropertyFactory(propertyWrapper);
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
	public Elements<String> keys() {
		return new MultiElements<>(Arrays.asList(Elements.of(super.getReadonlyMap().keySet()), propertyFactories.keys(),
				observable.keys()));
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
		return super.getReadonlyMap().containsKey(key) || propertyFactories.containsKey(key)
				|| observable.containsKey(key);
	}

	@Override
	public BroadcastEventRegistry<ChangeEvent<Elements<String>>> getKeyEventRegistry() {
		return (e) -> {
			return super.getKeyEventRegistry().registerListener(e)
					.and(propertyFactories.getKeyEventRegistry().registerListener(e))
					.and(observable.getKeyEventRegistry().registerListener(e));
		};
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

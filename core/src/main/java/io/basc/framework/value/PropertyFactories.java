package io.basc.framework.value;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.event.BroadcastNamedEventDispatcher;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.ChangeType;
import io.basc.framework.event.support.StandardBroadcastNamedEventDispatcher;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Registration;
import io.basc.framework.util.XUtils;

public class PropertyFactories implements PropertyFactory, Configurable {
	private final BroadcastNamedEventDispatcher<String, ChangeEvent<String>> namedEventDispatcher;
	private final ConfigurableServices<PropertyFactory> factories;

	public PropertyFactories() {
		this(new StandardBroadcastNamedEventDispatcher<>());
	}

	public PropertyFactories(BroadcastNamedEventDispatcher<String, ChangeEvent<String>> namedEventDispatcher) {
		this.namedEventDispatcher = namedEventDispatcher;
		this.factories = new ConfigurableServices<PropertyFactory>(PropertyFactory.class) {
			@Override
			protected boolean addService(PropertyFactory service, Collection<PropertyFactory> targetServices) {
				if (super.addService(service, targetServices)) {
					long t = System.currentTimeMillis();
					ConsumeProcessor.consumeAll(service.iterator(), (e) -> namedEventDispatcher.publishEvent(e,
							new ChangeEvent<String>(t, ChangeType.CREATE, e)));
					return true;
				}
				return false;
			}
		};
	}

	public ConfigurableServices<PropertyFactory> getFactories() {
		return factories;
	}

	@Override
	public boolean isConfigured() {
		return factories.isConfigured();
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		factories.configure(serviceLoaderFactory);
	}

	@Override
	public Value get(String key) {
		for (PropertyFactory factory : factories) {
			if (factory == null || factory == this) {
				continue;
			}

			Value value = factory.get(key);
			if (value != null && value.isPresent()) {
				return value;
			}
		}
		return Value.EMPTY;
	}

	@Override
	public boolean containsKey(String key) {
		for (PropertyFactory factory : factories) {
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
	public Iterator<String> iterator() {
		return stream().iterator();
	}

	@Override
	public Stream<String> stream() {
		Stream<String> stream = null;
		for (PropertyFactory factory : factories) {
			stream = stream == null ? factory.stream() : Stream.concat(stream, factory.stream());
		}
		return stream == null ? XUtils.emptyStream() : stream.distinct();
	}

	@Override
	public Registration registerListener(String name, EventListener<ChangeEvent<String>> eventListener) {
		return namedEventDispatcher.registerListener(name, eventListener);
	}
}

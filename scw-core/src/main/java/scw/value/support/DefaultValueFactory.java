package scw.value.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import scw.core.Assert;
import scw.core.OrderComparator;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.MultiEventRegistration;
import scw.event.support.DefaultNamedEventDispatcher;
import scw.event.support.ObservableMap;
import scw.util.CollectionFactory;
import scw.util.Pair;
import scw.value.ConfigurableValueFactory;
import scw.value.Value;
import scw.value.ValueFactory;

public class DefaultValueFactory<K, F extends ValueFactory<K>> implements ConfigurableValueFactory<K> {
	private final ObservableMap<K, Value> valueMap;
	private final List<F> factories;

	public DefaultValueFactory(boolean concurrent) {
		this.valueMap = new ObservableMap<K, Value>(concurrent,
				new DefaultNamedEventDispatcher<K, ChangeEvent<Pair<K, Value>>>(concurrent));
		this.factories = CollectionFactory.createArrayList(concurrent, 8);
	}

	public void addFactory(F factory) {
		if (factory == null) {
			return;
		}

		factories.add(factory);
		Collections.sort(factories, OrderComparator.INSTANCE);
	}

	public ObservableMap<K, Value> getValueMap() {
		return valueMap;
	}

	public Iterator<F> getFactories() {
		return factories.iterator();
	}

	public Value getValue(K key) {
		Value value = valueMap.get(key);
		if (value != null) {
			return value;
		}

		Iterator<F> iterator = getFactories();
		while (iterator.hasNext()) {
			ValueFactory<K> valueFactory = iterator.next();
			if (valueFactory == null) {
				continue;
			}
			value = valueFactory.getValue(key);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	public boolean containsKey(K key) {
		if (valueMap.containsKey(key)) {
			return true;
		}

		Iterator<F> iterator = getFactories();
		while (iterator.hasNext()) {
			if (iterator.next().containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	public EventRegistration registerListener(final K key, final EventListener<ChangeEvent<K>> eventListener) {
		EventRegistration registration1 = this.valueMap.getEventDispatcher().registerListener(key,
				new EventListener<ChangeEvent<Pair<K, Value>>>() {

					public void onEvent(ChangeEvent<Pair<K, Value>> event) {
						eventListener.onEvent(new ChangeEvent<K>(event, event.getSource().getKey()));
					}
				});

		if (factories.size() == 0) {
			return registration1;
		}

		List<EventRegistration> registrations = new ArrayList<EventRegistration>(factories.size());
		registrations.add(registration1);
		Iterator<F> iterator = getFactories();
		while (iterator.hasNext()) {
			F factory = iterator.next();
			EventRegistration registration = factory.registerListener(key, eventListener);
			registrations.add(registration);
		}
		return new MultiEventRegistration(registrations.toArray(new EventRegistration[0]));
	}

	public boolean remove(K key) {
		Assert.requiredArgument(key != null, "key");
		Value value = valueMap.remove(key);
		return value != null;
	}

	public boolean put(K key, Value value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		valueMap.put(key, value);
		return true;
	}

	public boolean putIfAbsent(K key, Value value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return valueMap.putIfAbsent(key, value) == null;
	}

	public void clear() {
		valueMap.clear();
	}
}

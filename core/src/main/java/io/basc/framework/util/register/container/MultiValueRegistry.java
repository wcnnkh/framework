package io.basc.framework.util.register.container;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.register.KeyValueRegistry;
import io.basc.framework.util.register.LifecycleRegistrationWrapper;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.StandardRegistrationWrapper;
import lombok.NonNull;

public class MultiValueRegistry<K, V, L extends Collection<ElementRegistration<V>>, M extends Map<K, ElementRegistry<V, L>>>
		extends LazyContainer<M> implements KeyValueRegistry<K, V> {
	private static class InternalEntryRegistration<K, V> extends StandardRegistrationWrapper<ElementRegistration<V>>
			implements EntryRegistration<K, V>, LifecycleRegistrationWrapper<ElementRegistration<V>> {
		private final K key;

		public InternalEntryRegistration(K key, @NonNull ElementRegistration<V> source) {
			super(source, Elements.empty());
			this.key = key;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return getSource().getPayload();
		}

		@Override
		public V setValue(V value) {
			return getSource().setPayload(value);
		}

		@Override
		public EntryRegistration<K, V> and(Registration registration) {
			return EntryRegistration.super.and(registration);
		}
	}

	@NonNull
	private final Publisher<? super Elements<ChangeEvent<KeyValue<K, V>>>> changeEventsPublisher;

	@NonNull
	private final Supplier<? extends L> valuesSupplier;

	public MultiValueRegistry(@NonNull Supplier<? extends M> containerSupplier,
			@NonNull Supplier<? extends L> valuesSupplier,
			@NonNull Publisher<? super Elements<ChangeEvent<KeyValue<K, V>>>> changeEventsPublisher) {
		super(containerSupplier);
		this.valuesSupplier = valuesSupplier;
		this.changeEventsPublisher = changeEventsPublisher;
	}

	public final void cleanup() {
		execute((map) -> {
			// 清理已经为空的元素注册器
			map.entrySet().removeIf((entry) -> entry.getValue().getRegistrations().isCancelled());
			return true;
		});
	}

	@Override
	public Elements<KeyValue<K, V>> getElements() {
		return read((map) -> {
			if (map == null) {
				return Elements.empty();
			}

			// 使用copy保证线程安全
			// TODO 是否可以使用分段锁实现？
			return Elements
					.of(map.entrySet().stream()
							.flatMap((entry) -> entry.getValue().getElements()
									.map((value) -> KeyValue.of(entry.getKey(), value)).stream())
							.collect(Collectors.toList()));
		});
	}

	public final ElementRegistry<V, L> newValues(K key) {
		return new ElementRegistry<>(valuesSupplier, (events) -> {
			Elements<ChangeEvent<KeyValue<K, V>>> entryChangeEvent = events.map((e) -> {
				return e.convert((v) -> KeyValue.of(key, v));
			});
			return changeEventsPublisher.publish(entryChangeEvent);
		});
	}

	@Override
	public final EntryRegistration<K, V> register(KeyValue<K, V> element) throws RegistrationException {
		return write((map) -> {
			ElementRegistry<V, L> values = map.get(element.getKey());
			if (values == null) {
				values = newValues(element.getKey());
			}
			map.put(element.getKey(), values);
			ElementRegistration<V> elementRegistration = values.register(element.getValue());
			return new InternalEntryRegistration<>(element.getKey(), elementRegistration);
		});
	}

	@Override
	public Registration registers(Iterable<? extends KeyValue<K, V>> elements) throws RegistrationException {
		return Registration.registers(elements, this::register);
	}

	public final ElementRegistry<V, L> getElementRegistry(Object key) {
		return read((map) -> {
			if (map == null) {
				return null;
			}

			return map.get(key);
		});
	}

	@Override
	public Elements<KeyValue<K, V>> getElements(K key) {
		return read((map) -> {
			if (map == null) {
				return Elements.empty();
			}

			ElementRegistry<V, L> values = getElementRegistry(key);
			return values == null ? Elements.empty() : values.getElements().map(((value) -> KeyValue.of(key, value)));
		});
	}

	public final boolean isEmpty() {
		return readAsBoolean((map) -> {
			if (map == null) {
				return true;
			}

			if (map.isEmpty()) {
				return true;
			}

			return map.entrySet().stream().allMatch((e) -> e.getValue().isEmpty());
		});
	}

	public void clear() {
		execute((map) -> {
			Iterator<Entry<K, ElementRegistry<V, L>>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<K, ElementRegistry<V, L>> entry = iterator.next();
				entry.getValue().clear();
				iterator.remove();
			}
			return true;
		});
	}

	@Override
	public Receipt deregisterKeys(Iterable<? extends K> keys) {
		return update((map) -> {
			if (map == null) {
				return Receipt.fail();
			}

			Receipt receipt = Receipt.fail();
			for (K key : keys) {
				ElementRegistry<V, L> values = map.remove(key);
				if (values == null) {
					continue;
				}

				// TODO 想办法合并通知事件
				values.clear();
				receipt = Receipt.success();
			}
			return receipt;
		});
	}
}

package io.basc.framework.util.register.container;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.match.Matcher;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.register.KeyValueRegistry;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.RegistrationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class MultiValueRegistry<K, V, L extends Collection<ElementRegistration<V>>, M extends Map<K, ElementRegistry<V, L>>>
		extends LazyContainer<M> implements KeyValueRegistry<K, V>, Registration {
	@RequiredArgsConstructor
	private static class InternalEntryRegistration<K, V> implements EntryRegistration<K, V> {
		private final ElementRegistration<V> elementRegistration;
		private final K key;

		@Override
		public void deregister() throws RegistrationException {
			elementRegistration.deregister();
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return elementRegistration.getValue();
		}

		@Override
		public boolean isInvalid() {
			return elementRegistration.isInvalid();
		}

		@Override
		public V setValue(V value) {
			return elementRegistration.setValue(value);
		}
	}

	@NonNull
	private final EventPublishService<ChangeEvent<KeyValue<K, V>>> eventPublishService;

	@NonNull
	private final Supplier<? extends L> valuesSupplier;

	public MultiValueRegistry(@NonNull Supplier<? extends M> containerSupplier,
			@NonNull Supplier<? extends L> valuesSupplier,
			@NonNull EventPublishService<ChangeEvent<KeyValue<K, V>>> eventPublishService) {
		super(containerSupplier);
		this.valuesSupplier = valuesSupplier;
		this.eventPublishService = eventPublishService;
	}

	public final void cleanup() {
		execute((map) -> {
			// 清理已经为空的元素注册器
			map.entrySet().removeIf((entry) -> entry.getValue().getRegistrations().isInvalid());
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
				KeyValue<K, V> source = KeyValue.of(key, e.getSource());
				KeyValue<K, V> parentSource = KeyValue.of(key, e.getParentSource());
				return new ChangeEvent<>(source, e, e.getChangeType(), parentSource);
			});
			eventPublishService.publishBatchEvents(entryChangeEvent);
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
			return new InternalEntryRegistration<>(elementRegistration, element.getKey());
		});
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
	public Elements<KeyValue<K, V>> getElements(K key, Matcher<? super K> matcher) {
		return read((map) -> {
			if (map == null) {
				return Elements.empty();
			}

			if (matcher.isPattern(key)) {
				return Elements
						.of(map.entrySet().stream().filter((e) -> matcher.match(key, e.getKey()))
								.collect(Collectors.toList()))
						.flatMap((e) -> e.getValue().getElements().map((value) -> KeyValue.of(e.getKey(), value)));
			} else {
				ElementRegistry<V, L> values = getElementRegistry(key);
				return values == null ? Elements.empty()
						: values.getElements().map(((value) -> KeyValue.of(key, value)));
			}
		});
	}

	@Override
	public final boolean isInvalid() {
		return readAsBoolean((map) -> {
			if (map == null) {
				return true;
			}

			if (map.isEmpty()) {
				return true;
			}

			return map.entrySet().stream().allMatch((e) -> e.getValue().isInvalid());
		});
	}

	@Override
	public final void deregister() throws RegistrationException {
		execute((map) -> {
			Iterator<Entry<K, ElementRegistry<V, L>>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<K, ElementRegistry<V, L>> entry = iterator.next();
				entry.getValue().deregister();
				iterator.remove();
			}
			return true;
		});
	}
}

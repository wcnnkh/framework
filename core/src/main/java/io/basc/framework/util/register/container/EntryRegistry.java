package io.basc.framework.util.register.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.util.Elements;
import io.basc.framework.util.EmptyRegistrations;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Registrations;
import io.basc.framework.util.Streams;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.ChangeType;
import io.basc.framework.util.register.KeyValueRegistry;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.ServiceRegistry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
public class EntryRegistry<K, V, M extends Map<K, EntryRegistration<K, V>>> extends LazyContainer<M>
		implements KeyValueRegistry<K, V>, ServiceRegistry<KeyValue<K, V>> {
	@RequiredArgsConstructor
	private class BatchRegistrations implements Registrations<EntryRegistration<K, V>> {
		private final Elements<UpdateableEntryRegistration> registrations;

		@Override
		public boolean cancel() {
			Elements<UpdateableEntryRegistration> elements = this.registrations.filter((e) -> !e.isCancelled());
			// 全部设置为无效，防止调用默认的事件
			elements.forEach((e) -> e.getLimiter().limited());
			batchDeregister(elements);
			return true;
		}

		@Override
		public Elements<EntryRegistration<K, V>> getElements() {
			return registrations.map((e) -> e);
		}
	}

	@RequiredArgsConstructor
	private static class ChangeInfo<K, V> {
		private final ChangeEvent<KeyValue<K, V>> event;
		private final EntryRegistration<K, V> registration;
	}

	private class InternalElements implements Elements<KeyValue<K, V>> {

		@Override
		public void forEach(Consumer<? super KeyValue<K, V>> action) {
			read((map) -> {
				if (map == null) {
					return null;
				}

				map.entrySet().stream().map((e) -> e.getValue()).forEach(action);
				return null;
			});
		}

		@Override
		public Iterator<KeyValue<K, V>> iterator() {
			return read((map) -> map == null ? Collections.emptyIterator()
					: map.values().stream().map((e) -> (KeyValue<K, V>) e).collect(Collectors.toList()).iterator());
		}

		@Override
		public Stream<KeyValue<K, V>> stream() {
			// copy一下来保证线程安全
			return read((map) -> map == null ? Streams.empty()
					: map.values().stream().map((e) -> (KeyValue<K, V>) e).collect(Collectors.toList()).stream());
		}
	}

	private class UpdateableEntryRegistration extends StandardEntryRegistrationWrapper<K, V, EntryRegistration<K, V>> {
		private UpdateableEntryRegistration(
				StandardEntryRegistrationWrapper<K, V, EntryRegistration<K, V>> combinableServiceRegistration) {
			super(combinableServiceRegistration);
		}

		public UpdateableEntryRegistration(EntryRegistration<K, V> source) {
			super(source, Elements.empty());
		}

		@Override
		public UpdateableEntryRegistration and(@NonNull Registration registration) {
			return new UpdateableEntryRegistration(super.and(registration));
		}

		@Override
		public boolean cancel(BooleanSupplier cancel) {
			return super.cancel(() -> {
				update((map) -> map == null ? null : map.remove(getKey(), getValue()));
				changeEventsPublisher.publish(Elements.singleton(new ChangeEvent<>(this, ChangeType.DELETE)));
				return true;
			});
		}

		@Override
		public V setValue(V value) {
			V oldValue = super.setValue(value);
			KeyValue<K, V> oldKeyValue = oldValue == null ? null : KeyValue.of(getKey(), oldValue);
			changeEventsPublisher
					.publish(Elements.singleton(new ChangeEvent<>(oldKeyValue, KeyValue.of(getKey(), value))));
			return oldValue;
		}
	}

	@NonNull
	private final Publisher<? super Elements<ChangeEvent<KeyValue<K, V>>>> changeEventsPublisher;

	public EntryRegistry(Supplier<? extends M> containerSupplier,
			@NonNull Publisher<? super Elements<ChangeEvent<KeyValue<K, V>>>> changeEventsPublisher) {
		super(containerSupplier);
		this.changeEventsPublisher = changeEventsPublisher;
	}

	@Override
	public Receipt deregisters(Iterable<? extends KeyValue<K, V>> services) {
		return update((map) -> {
			if (map == null) {
				return Receipt.fail();
			}

			List<EntryRegistration<K, V>> removes = new ArrayList<>();
			for (KeyValue<K, V> kv : services) {
				if (!map.containsKey(kv.getKey())) {
					continue;
				}

				EntryRegistration<K, V> registration = map.get(kv.getKey());
				if (registration == null || !ObjectUtils.equals(kv.getValue(), registration.getValue())) {
					continue;
				}

				registration = map.remove(kv.getKey());
				if (registration != null) {
					removes.add(registration);
				}
			}

			if (removes.isEmpty()) {
				return Receipt.fail();
			}

			return batchDeregister(Elements.of(removes));
		});
	}

	protected final Receipt batchDeregister(Elements<? extends EntryRegistration<K, V>> registrations) {
		if (registrations.isEmpty()) {
			return Receipt.fail();
		}

		registrations.forEach(Registration::cancel);
		execute((map) -> {
			registrations.forEach((e) -> map.remove(e.getKey(), e.getValue()));
			return true;
		});
		Elements<ChangeEvent<KeyValue<K, V>>> events = registrations
				.map((e) -> new ChangeEvent<>(e, ChangeType.DELETE));
		return changeEventsPublisher.publish(events);
	}

	private final Registrations<EntryRegistration<K, V>> convertToBatchRegistration(
			Elements<EntryRegistration<K, V>> registrations) {
		if (registrations == null || registrations.isEmpty()) {
			return EmptyRegistrations.empty();
		}

		Elements<UpdateableEntryRegistration> updateableRegistrations = registrations
				.map((e) -> new UpdateableEntryRegistration(e));
		return new BatchRegistrations(updateableRegistrations);
	}

	@Override
	public Elements<KeyValue<K, V>> getElements() {
		return new InternalElements();
	}

	@Override
	public Elements<KeyValue<K, V>> getElements(K key) {
		return read((map) -> {
			if (map == null) {
				return Elements.empty();
			}

			EntryRegistration<K, V> registration = map.get(key);
			return registration == null ? Elements.empty() : Elements.singleton(registration);
		});
	}

	public final Entry<K, V> getEntry(
			@NonNull Function<? super M, ? extends Entry<K, EntryRegistration<K, V>>> getter) {
		return read((map) -> {
			if (map == null) {
				return null;
			}

			Entry<K, EntryRegistration<K, V>> entry = getter.apply(map);
			if (entry == null) {
				return null;
			}

			return entry.getValue();
		});
	}

	public final EntryRegistration<K, V> getRegistration(
			@NonNull Function<? super M, ? extends EntryRegistration<K, V>> reader) {
		EntryRegistration<K, V> registration = read(reader);
		if (registration == null) {
			return null;
		}
		return new UpdateableEntryRegistration(registration);
	}

	public final Registrations<EntryRegistration<K, V>> getRegistrations(
			Function<? super M, ? extends Elements<EntryRegistration<K, V>>> reader) {
		Elements<EntryRegistration<K, V>> registrations = read(
				(collection) -> collection == null ? Elements.empty() : reader.apply(collection));
		return convertToBatchRegistration(registrations);
	}

	protected final AtomicEntryRegistration<K, V> newRegistration(KeyValue<K, V> keyValue) {
		return new AtomicEntryRegistration<>(keyValue.getKey(), keyValue.getValue());
	}

	public final V getValue(Function<? super M, ? extends EntryRegistration<K, V>> getter) {
		return read((map) -> {
			if (map == null) {
				return null;
			}

			EntryRegistration<K, V> entryRegistration = getter.apply(map);
			return entryRegistration == null ? null : entryRegistration.getValue();
		});
	}

	@Override
	public Registration registers(Iterable<? extends KeyValue<K, V>> elements) throws RegistrationException {
		return batchRegister(elements);
	}

	public final Registrations<EntryRegistration<K, V>> batchRegister(Iterable<? extends KeyValue<K, V>> elements)
			throws RegistrationException {
		Elements<ChangeInfo<K, V>> changes = write((map) -> {
			return Elements.of(elements).map((keyValue) -> {
				EntryRegistration<K, V> registration = newRegistration(keyValue);
				EntryRegistration<K, V> old = map.put(keyValue.getKey(), registration);
				ChangeEvent<KeyValue<K, V>> event;
				if (old == null) {
					// created
					event = new ChangeEvent<KeyValue<K, V>>(keyValue, ChangeType.CREATE);
				} else {
					// update
					event = new ChangeEvent<>(old, keyValue);
				}

				return new ChangeInfo<K, V>(event, registration);
				// 调用toList可以执行map中的内容
			}).toList();
		});
		changeEventsPublisher.publish(changes.map((e) -> e.event).toList());
		return convertToBatchRegistration(changes.map((e) -> e.registration).toList());
	}

	public Registrations<EntryRegistration<K, V>> getRegistrations() {
		return getRegistrations((map) -> Elements.of(map.values()));
	}

	@Override
	public Receipt deregisterKeys(Iterable<? extends K> keys) {
		return update((map) -> {
			if (map == null) {
				return Receipt.fail();
			}

			List<EntryRegistration<K, V>> removes = new ArrayList<>();
			for (K key : keys) {
				EntryRegistration<K, V> registration = map.remove(key);
				if (registration != null) {
					removes.add(registration);
				}
			}

			if (removes.isEmpty()) {
				return Receipt.fail();
			}

			return batchDeregister(Elements.of(removes));
		});
	}
}

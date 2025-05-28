package run.soeasy.framework.core.exchange.container.map;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.MultiValueMap;
import run.soeasy.framework.core.concurrent.AtomicEntry;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Receipts;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.AbstractContainer;
import run.soeasy.framework.core.exchange.container.Container;
import run.soeasy.framework.core.exchange.container.KeyValueRegistration;
import run.soeasy.framework.core.exchange.container.KeyValueRegistry;
import run.soeasy.framework.core.exchange.container.PayloadRegistration;
import run.soeasy.framework.core.exchange.container.RegistrationException;
import run.soeasy.framework.core.function.ThrowingSupplier;

public class MultiValueMapContainer<K, V, R extends PayloadRegistration<V>, VC extends Container<V, R>, M extends Map<K, VC>>
		extends AbstractContainer<M, KeyValue<K, V>, KeyValueRegistration<K, V>>
		implements MultiValueMap<K, V>, KeyValueRegistry<K, V> {
	private final Function<? super K, ? extends VC> valuesCreator;

	public MultiValueMapContainer(@NonNull ThrowingSupplier<? extends M, ? extends RuntimeException> containerSource,
			@NonNull Function<? super K, ? extends VC> valuesCreator) {
		super(containerSource);
		this.valuesCreator = valuesCreator;
	}

	protected VC newValues(K key) {
		return valuesCreator.apply(key);
	}

	@Override
	public final void adds(K key, List<V> values) {
		write((map) -> {
			VC services = map.get(key);
			if (services == null) {
				services = newValues(key);
				map.put(key, services);
			}
			services.registers(Elements.of(values));
			return null;
		});
	}

	@Override
	public final void clear() {
		update((e) -> {
			if (e == null) {
				return null;
			}

			reset();
			e.clear();
			return null;
		});
	}

	@Override
	public boolean containsKey(Object key) {
		return readAsBoolean((map) -> map == null ? false : map.containsKey(key));
	}

	@Override
	public final boolean containsValue(Object value) {
		return readAsBoolean((map) -> map == null ? false : map.containsValue(value));
	}

	public final Receipt deregister(K key, V value) throws RegistrationException {
		return update((map) -> {
			if (map == null) {
				return Receipt.FAILURE;
			}

			VC vc = map.get(key);
			if (vc == null) {
				return Receipt.FAILURE;
			}

			return vc.deregister(value);
		});
	}

	@Override
	public final Receipt deregister(KeyValue<K, V> element) {
		return deregister(element.getKey(), element.getValue());
	}

	@Override
	public final Receipt deregisterKey(K key) {
		return update((map) -> {
			if (map == null) {
				return Receipt.FAILURE;
			}

			VC vc = map.remove(key);
			if (vc == null) {
				return Receipt.FAILURE;
			}

			vc.reset();
			return Receipt.SUCCESS;
		});
	}

	@Override
	public final Receipt deregisterKeys(Iterable<? extends K> keys) {
		return Receipts.of(Elements.of(keys).map((key) -> deregisterKey(key)).toList());
	}

	@Override
	public final Receipt deregisters(Elements<? extends KeyValue<K, V>> elements) {
		return Receipts.of(elements.map((e) -> deregister(e)).toList());
	}

	public Elements<KeyValue<K, V>> entries() {
		return readAsElements((map) -> {
			if (map == null) {
				return Elements.empty();
			}

			return Elements.of(() -> map.entrySet().stream()
					.flatMap((entry) -> entry.getValue().stream().map((e) -> KeyValue.of(entry.getKey(), e))));
		});
	}

	@Override
	public final Set<Entry<K, List<V>>> entrySet() {
		return readAsSet((map) -> {
			if (map == null) {
				return Collections.emptySet();
			}

			return map.entrySet().stream().map((e) -> {
				Entry<K, List<V>> entry = new AtomicEntry<>(e.getKey());
				entry.setValue(e.getValue().toList());
				return entry;
			}).collect(Collectors.toSet());
		});
	}

	@Override
	public final List<V> get(Object key) {
		return readAsList((map) -> {
			VC vc = map.get(key);
			if (vc == null) {
				return null;
			}
			return vc.toList();
		});
	}

	@Override
	public final Elements<KeyValueRegistration<K, V>> getElements() {
		return entries().map((e) -> KeyValueRegistration.of(e.getKey(), e.getValue(), (key, value) -> {
			Receipt receipt = deregister(key, value);
			return receipt.isSuccess();
		}));
	}

	@Override
	public final V getFirst(Object key) {
		return read((map) -> {
			if (map == null) {
				return null;
			}

			VC vc = map.get(key);
			if (vc == null) {
				return null;
			}

			return vc.first();
		});
	}

	@Override
	public boolean isEmpty() {
		return readAsBoolean((map) -> map == null ? false : map.isEmpty());
	}

	@Override
	public final Iterator<KeyValue<K, V>> iterator() {
		return entries().iterator();
	}

	@Override
	public final Set<K> keySet() {
		return readAsSet((e) -> e == null ? null : e.keySet());
	}

	@Override
	public final List<V> put(K key, List<V> value) {
		return writeAsList((map) -> {
			VC services = map.get(key);
			if (services == null) {
				services = newValues(key);
				services.registers(Elements.of(value));
				map.put(key, services);
				return null;
			} else {
				List<V> oldList = services.toList();
				services.reset();
				services.registers(Elements.of(value));
				return oldList;
			}
		});
	}

	@Override
	public final void putAll(Map<? extends K, ? extends List<V>> m) {
		for (Entry<? extends K, ? extends List<V>> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public final Registration register(K key, V value) throws RegistrationException {
		return write((map) -> {
			VC services = map.get(key);
			if (services == null) {
				services = newValues(key);
				map.put(key, services);
			}
			return services.register(value);
		});
	}

	@Override
	public final Registration register(KeyValue<K, V> element) throws RegistrationException {
		return register(element.getKey(), element.getValue());
	}

	@Override
	public final Registration registers(@NonNull Elements<? extends KeyValue<K, V>> elements)
			throws RegistrationException {
		return Registration.registers(elements, this::register);
	}

	@Override
	public final List<V> remove(Object key) {
		return updateAsList((map) -> {
			if (map == null) {
				return null;
			}

			VC vc = map.remove(key);
			if (vc == null) {
				return null;
			}

			List<V> list = vc.collect(Collectors.toList());
			vc.reset();
			return list;
		});
	}

	@Override
	public void reset() {
		update((map) -> {
			if (map == null) {
				return null;
			}

			for (Entry<K, VC> entry : map.entrySet()) {
				entry.getValue().reset();
			}
			return null;
		});
	}

	@Override
	public final void set(K key, V value) {
		write((map) -> {
			VC services = map.get(key);
			if (services == null) {
				services = newValues(key);
				map.put(key, services);
			}
			services.reset();
			services.register(value);
			return null;
		});
	}

	@Override
	public int size() {
		return readAsInt((map) -> map == null ? 0 : map.size());
	}

	@Override
	public final Stream<KeyValue<K, V>> stream() {
		return entries().stream();
	}

	@Override
	public final Collection<List<V>> values() {
		return readAsList((map) -> {
			if (map == null) {
				return Collections.emptyList();
			}

			return map.values().stream().map((e) -> e.toList()).collect(Collectors.toList());
		});
	}
}

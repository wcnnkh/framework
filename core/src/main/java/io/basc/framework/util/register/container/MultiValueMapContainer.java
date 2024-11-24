package io.basc.framework.util.register.container;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;
import io.basc.framework.util.collect.MultiValueMap;
import io.basc.framework.util.register.KeyValueRegistry;
import io.basc.framework.util.register.RegistrationException;
import lombok.NonNull;

public class MultiValueMapContainer<K, V, C extends Collection<ElementRegistration<V>>, L extends CollectionContainer<V, C>, M extends Map<K, L>>
		extends AbstractContainer<M, KeyValue<K, V>, EntryRegistration<K, V>>
		implements KeyValueRegistry<K, V>, MultiValueMap<K, V> {
	@NonNull
	private final Function<? super K, ? extends L> valuesCreator;

	public MultiValueMapContainer(@NonNull Supplier<? extends M> containerSupplier,
			@NonNull Function<? super K, ? extends L> valuesCreator) {
		super(containerSupplier);
		this.valuesCreator = valuesCreator;
	}

	@Override
	public Receipt deregisters(Iterable<? extends KeyValue<K, V>> elements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Registration registers(Iterable<? extends KeyValue<K, V>> elements) throws RegistrationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<KeyValue<K, V>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<KeyValue<K, V>> stream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Elements<EntryRegistration<K, V>> getElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<V> get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<V> put(K key, List<V> value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<V> remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends List<V>> m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<List<V>> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Entry<K, List<V>>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V getFirst(K key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(K key, V value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void set(K key, V value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAll(Map<K, V> values) {
		// TODO Auto-generated method stub

	}

	@Override
	public Receipt deregisterKeys(Iterable<? extends K> keys) {
		// TODO Auto-generated method stub
		return null;
	}

}

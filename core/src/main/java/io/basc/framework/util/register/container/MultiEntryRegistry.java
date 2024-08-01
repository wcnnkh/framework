package io.basc.framework.util.register.container;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

import io.basc.framework.util.collect.MultiValueMap;
import io.basc.framework.util.element.Elements;

public class MultiEntryRegistry<K, V, C extends Collection<ElementRegistration<V>>, L extends MultiValueRegistry<K, V, C>, M extends Map<K, L>>
		extends AbstractServiceRegistry<Entry<K, V>, M, EntryRegistration<K, V>, EntryBatchRegistration<K, V>>
		implements MultiValueMap<K, V> {

	public MultiEntryRegistry(Supplier<? extends M> containerSupplier) {
		super(containerSupplier);
	}

	@Override
	public Elements<EntryRegistration<K, V>> getRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EntryRegistration<K, V> createRegistration(Entry<K, V> item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EntryBatchRegistration<K, V> createRegistrations(Elements<EntryRegistration<K, V>> registrations) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EntryBatchRegistration<K, V> postRegisterAfter(EntryBatchRegistration<K, V> registrations) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean register(M container, EntryRegistration<K, V> registration) {
		// TODO Auto-generated method stub
		return false;
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

}

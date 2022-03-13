package io.basc.framework.orm.transfer;

import io.basc.framework.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransfColumns<K, V> extends ArrayList<Pair<K, V>> {
	private static final long serialVersionUID = 1L;

	public TransfColumns() {
		super();
	}

	public TransfColumns(int initialCapacity) {
		super(initialCapacity);
	}

	public TransfColumns(Collection<? extends Pair<K, V>> pairs) {
		super(pairs);
	}

	public TransfColumns(V[] values) {
		this(values == null ? 8 : values.length);
		for (V value : values) {
			addValue(value);
		}
	}

	public Stream<K> keys() {
		return stream().map((e) -> e.getKey());
	}

	public Stream<V> values() {
		return stream().map((e) -> e.getValue());
	}

	public boolean hasKeys() {
		return keys().filter((e) -> e != null).count() != 0;
	}

	public void add(K key, V value) {
		add(new Pair<K, V>(key, value));
	}

	public void addValue(V value) {
		add(new Pair<K, V>(null, value));
	}

	public List<K> getKeys() {
		return keys().collect(Collectors.toList());
	}

	public List<V> getValues() {
		return values().collect(Collectors.toList());
	}
}

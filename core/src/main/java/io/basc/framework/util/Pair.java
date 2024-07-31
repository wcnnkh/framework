package io.basc.framework.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import io.basc.framework.util.function.Processor;
import lombok.Data;

public class Pair<K, V> implements Serializable {
	private static final long serialVersionUID = 1L;
	private K key;
	private V value;

	public Pair() {
	}

	public Pair(Pair<K, V> pair) {
		this(pair.key, pair.value);
	}

	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public <A, B> Pair<A, B> of(A key, B value) {
		return new Pair<>(key, value);
	}

	public static <K, V, E extends Throwable> Optional<Pair<K, V>> process(Iterable<? extends K> keys,
			Processor<? super K, ? extends V, ? extends E> processor, Predicate<? super Pair<K, V>> returnTest)
			throws E {
		return process(keys == null ? Collections.emptyIterator() : keys.iterator(), processor, returnTest);
	}

	public static <K, V, E extends Throwable> Optional<Pair<K, V>> process(Iterator<? extends K> keys,
			Processor<? super K, ? extends V, ? extends E> processor, Predicate<? super Pair<K, V>> returnTest)
			throws E {
		Assert.requiredArgument(processor != null, "processor");
		if (keys == null) {
			return Optional.empty();
		}

		while (keys.hasNext()) {
			K key = keys.next();
			V value = processor.process(key);
			if (value == null) {
				continue;
			}

			Pair<K, V> pair = new Pair<K, V>(key, value);
			if (returnTest == null || returnTest.test(pair)) {
				return Optional.ofNullable(pair);
			}
		}
		return Optional.empty();
	}

	public static <K, V, E extends Throwable> List<Pair<K, V>> processAll(Iterable<? extends K> keys,
			Processor<? super K, ? extends V, ? extends E> processor, Predicate<? super Pair<K, V>> predicate)
			throws E {
		return processAll(keys == null ? Collections.emptyIterator() : keys.iterator(), processor, predicate);
	}

	public static <K, V, E extends Throwable> List<Pair<K, V>> processAll(Iterator<? extends K> keys,
			Processor<? super K, ? extends V, ? extends E> processor, Predicate<? super Pair<K, V>> predicate)
			throws E {
		Assert.requiredArgument(processor != null, "processor");
		Assert.requiredArgument(predicate != null, "predicate");
		if (keys == null) {
			return Collections.emptyList();
		}

		List<Pair<K, V>> list = new ArrayList<>();
		while (keys.hasNext()) {
			K key = keys.next();
			V value = processor.process(key);
			Pair<K, V> pair = new Pair<K, V>(key, value);
			if (predicate.test(pair)) {
				list.add(pair);
			}
		}
		return list.isEmpty() ? Collections.emptyList() : list;
	}
}

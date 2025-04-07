/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package run.soeasy.framework.util.collection;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Extension of the {@code Map} interface that stores multiple values.
 *
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {
	@FunctionalInterface
	public static interface MultiValueMapWrapper<K, V, W extends MultiValueMap<K, V>>
			extends MultiValueMap<K, V>, MapWrapper<K, List<V>, W> {
		@Override
		default V getFirst(Object key) {
			return getSource().getFirst(key);
		}

		@Override
		default void adds(K key, List<V> values) {
			getSource().adds(key, values);
		}

		@Override
		default void set(K key, V value) {
			getSource().set(key, value);
		}

		@Override
		default void add(K key, V value) {
			getSource().add(key, value);
		}

		@Override
		default void setAll(Map<? extends K, ? extends V> map) {
			getSource().setAll(map);
		}

		@Override
		default void addAll(Map<? extends K, ? extends List<V>> map) {
			getSource().addAll(map);
		}

		@Override
		default Map<K, V> toSingleValueMap() {
			return getSource().toSingleValueMap();
		}

	}

	/**
	 * Return the first value for the given key.
	 * 
	 * @param key the key
	 * @return the first value for the specified key, or {@code null}
	 */
	default V getFirst(Object key) {
		List<V> values = get(key);
		if (values == null || values.isEmpty()) {
			return null;
		}
		return values.get(0);
	}

	/**
	 * Add the given single value to the current list of values for the given key.
	 * 
	 * @param key   the key
	 * @param value the value to be added
	 */
	default void add(K key, V value) {
		adds(key, Arrays.asList(value));
	}

	void adds(K key, List<V> values);

	/**
	 * Set the given single value under the given key.
	 * 
	 * @param key   the key
	 * @param value the value to set
	 */
	void set(K key, V value);

	/**
	 * Set the given values under.
	 * 
	 * @param values the values.
	 */
	default void setAll(Map<? extends K, ? extends V> map) {
		for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
			set(entry.getKey(), entry.getValue());
		}
	}

	default void addAll(Map<? extends K, ? extends List<V>> map) {
		for (Entry<? extends K, ? extends List<V>> entry : map.entrySet()) {
			adds(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Returns the first values contained in this {@code MultiValueMap}.
	 * 
	 * @return a single value representation of this map
	 */
	default Map<K, V> toSingleValueMap() {
		if (isEmpty()) {
			return Collections.emptyMap();
		}

		Map<K, V> singleValueMap = new LinkedHashMap<K, V>(size());
		for (java.util.Map.Entry<K, List<V>> entry : entrySet()) {
			List<V> values = entry.getValue();
			if (CollectionUtils.isEmpty(values)) {
				continue;
			}

			singleValueMap.put(entry.getKey(), values.get(0));
		}
		return singleValueMap;
	}

}

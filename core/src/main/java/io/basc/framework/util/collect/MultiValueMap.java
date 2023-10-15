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

package io.basc.framework.util.collect;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.basc.framework.util.CollectionUtils;

/**
 * Extension of the {@code Map} interface that stores multiple values.
 *
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {

	/**
	 * Return the first value for the given key.
	 * 
	 * @param key the key
	 * @return the first value for the specified key, or {@code null}
	 */
	V getFirst(K key);

	/**
	 * Add the given single value to the current list of values for the given key.
	 * 
	 * @param key   the key
	 * @param value the value to be added
	 */
	void add(K key, V value);

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
	void setAll(Map<K, V> values);

	default void addAll(Map<K, List<V>> map) {
		for (Entry<K, List<V>> entry : map.entrySet()) {
			List<V> values = entry.getValue();
			if (values == null) {
				continue;
			}

			for (V value : values) {
				add(entry.getKey(), value);
			}
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
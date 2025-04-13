/*
 * Copyright 2002-2010 the original author or authors.
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

package run.soeasy.framework.core.collection;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import lombok.NonNull;

/**
 * Simple implementation of {@link MultiValueMap} that wraps a
 * {@link LinkedHashMap}, storing multiple values in a {@link LinkedList}.
 *
 * <p>
 * This Map implementation is generally not thread-safe. It is primarily
 * designed for data structures exposed from request objects, for use in a
 * single thread only.
 *
 */
public class LinkedMultiValueMap<K, V> extends DefaultMultiValueMap<K, V, LinkedHashMap<K, List<V>>>
		implements Serializable {
	private static final long serialVersionUID = 3801124242820219131L;

	public LinkedMultiValueMap() {
		this(new LinkedHashMap<>());
	}

	public LinkedMultiValueMap(int initialCapacity) {
		this(new LinkedHashMap<>(initialCapacity));
	}

	public LinkedMultiValueMap(int initialCapacity, float loadFactor) {
		this(new LinkedHashMap<>(initialCapacity, loadFactor));
	}

	public LinkedMultiValueMap(int initialCapacity, float loadFactor, boolean accessOrder) {
		this(new LinkedHashMap<>(initialCapacity, loadFactor, accessOrder));
	}

	public LinkedMultiValueMap(@NonNull LinkedHashMap<K, List<V>> source) {
		super(source);
	}

}

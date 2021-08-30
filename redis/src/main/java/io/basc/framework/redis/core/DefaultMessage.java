/*
 * Copyright 2011-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.basc.framework.redis.core;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

/**
 * Default message implementation.
 *
 * @author Costin Leau
 * @author Christoph Strobl
 * @author Mark Paluch
 */
public class DefaultMessage<K, V> implements Message<K, V> {
	private static final long serialVersionUID = 1L;
	private final K channel;
	private final V body;
	private @Nullable String toString;

	public DefaultMessage(K channel, V body) {

		Assert.notNull(channel, "Channel must not be null!");
		Assert.notNull(body, "Body must not be null!");

		this.body = body;
		this.channel = channel;
	}

	@Override
	public K getChannel() {
		return channel;
	}

	@Override
	public V getBody() {
		return body;
	}

	@Override
	public String toString() {
		return "channel:" + channel + ", body:" + body;
	}
}

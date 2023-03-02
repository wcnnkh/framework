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
package io.basc.framework.redis;

import io.basc.framework.lang.Nullable;

/**
 * Exception thrown when issuing commands on a connection that is subscribed and
 * waiting for events.
 * 
 * @author shuchaowen
 *
 */
public class RedisSubscribedConnectionException extends RedisSystemException {
	private static final long serialVersionUID = 1L;

	public RedisSubscribedConnectionException(@Nullable String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}

	public RedisSubscribedConnectionException(String msg) {
		super(msg);
	}
}

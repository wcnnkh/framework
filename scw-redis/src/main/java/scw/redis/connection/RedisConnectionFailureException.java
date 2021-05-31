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
package scw.redis.connection;

/**
 * Fatal exception thrown when the Redis connection fails completely.
 */
public class RedisConnectionFailureException extends RedisSystemException {
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg the detail message.
	 */
	public RedisConnectionFailureException(String msg) {
		super(msg);
	}

	/**
	 * @param msg   the detail message.
	 * @param cause the nested exception.
	 */
	public RedisConnectionFailureException(String msg, Throwable cause) {
		super(msg, cause);
	}

}

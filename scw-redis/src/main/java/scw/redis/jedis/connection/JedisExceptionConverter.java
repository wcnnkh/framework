/*
 * Copyright 2013-2021 the original author or authors.
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
package scw.redis.jedis.connection;

import java.io.IOException;
import java.net.UnknownHostException;

import redis.clients.jedis.exceptions.JedisClusterMaxAttemptsException;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.exceptions.JedisRedirectionException;
import scw.convert.Converter;
import scw.redis.core.ClusterRedirectException;
import scw.redis.core.RedisConnectionFailureException;
import scw.redis.core.RedisSystemException;
import scw.redis.core.TooManyClusterRedirectionsException;

public class JedisExceptionConverter implements Converter<Exception, RedisSystemException> {

	public RedisSystemException convert(Exception ex) {

		if (ex instanceof RedisSystemException) {
			return (RedisSystemException) ex;
		}

		if (ex instanceof JedisClusterMaxAttemptsException) {
			return new TooManyClusterRedirectionsException(ex.getMessage(), ex);
		}

		if (ex instanceof JedisRedirectionException) {
			JedisRedirectionException re = (JedisRedirectionException) ex;
			return new ClusterRedirectException(re.getSlot(), re.getTargetNode().getHost(),
					re.getTargetNode().getPort(), ex);
		}

		if (ex instanceof JedisConnectionException) {
			return new RedisConnectionFailureException(ex.getMessage(), ex);
		}

		if (ex instanceof JedisException) {
			return new RedisSystemException(ex.getMessage(), ex);
		}

		if (ex instanceof UnknownHostException) {
			return new RedisConnectionFailureException("Unknown host " + ex.getMessage(), ex);
		}

		if (ex instanceof IOException) {
			return new RedisConnectionFailureException("Could not connect to Redis server", ex);
		}

		return null;
	}
}

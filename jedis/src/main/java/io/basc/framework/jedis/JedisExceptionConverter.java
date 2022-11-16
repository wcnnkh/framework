package io.basc.framework.jedis;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.function.Function;

import io.basc.framework.redis.ClusterRedirectException;
import io.basc.framework.redis.RedisConnectionFailureException;
import io.basc.framework.redis.RedisSystemException;
import io.basc.framework.redis.TooManyClusterRedirectionsException;
import redis.clients.jedis.exceptions.JedisClusterOperationException;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.exceptions.JedisRedirectionException;

public class JedisExceptionConverter implements Function<Exception, RedisSystemException> {

	public RedisSystemException apply(Exception ex) {

		if (ex instanceof RedisSystemException) {
			return (RedisSystemException) ex;
		}

		if (ex instanceof JedisClusterOperationException) {
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

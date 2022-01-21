package io.basc.framework.redis.convert;

import java.util.List;

import io.basc.framework.redis.RedisPipeline;
import io.basc.framework.redis.RedisPipelineCommands;
import io.basc.framework.redis.RedisSystemException;

public interface ConvertibleRedisPipeline<SK, K, SV, V>
		extends ConvertibleRedisPipelineCommands<SK, K, SV, V>, RedisPipeline<K, V> {

	RedisPipeline<SK, SV> getSourceRedisPipeline();

	@Override
	default RedisPipelineCommands<SK, SV> getSourceRedisCommands() {
		return getSourceRedisPipeline();
	}

	@Override
	default void close() throws RedisSystemException {
		getSourceRedisPipeline().close();
	}

	@Override
	default List<Object> exec() throws RedisSystemException {
		return getSourceRedisPipeline().exec();
	}
}

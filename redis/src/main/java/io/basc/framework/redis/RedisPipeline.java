package io.basc.framework.redis;

import java.io.Closeable;
import java.util.List;

public interface RedisPipeline<K, V> extends RedisPipelineCommands<K, V>, Closeable {

	List<Object> exec() throws RedisSystemException;

	void close() throws RedisSystemException;

	boolean isClosed();
}

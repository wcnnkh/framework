package io.basc.framework.redis;

import java.io.Closeable;
import java.util.List;

/**
 * redis管道
 * 
 * @author wcnnkh
 *
 * @param <K>
 * @param <V>
 */
public interface RedisPipeline<K, V> extends RedisPipelineCommands<K, V>, Closeable {

	List<Object> exec() throws RedisSystemException;

	/**
	 * 执行并关闭管道
	 */
	void close() throws RedisSystemException;

	boolean isClosed();
}

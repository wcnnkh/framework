package io.basc.framework.redis.async;

/**
 * redis队列
 * 
 * @author wcnnkh
 *
 * @param <K>
 * @param <V>
 */
public interface RedisPipeline<K, V> extends AsyncRedisCommands<K, V> {
}

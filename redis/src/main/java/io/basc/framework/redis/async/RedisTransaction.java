package io.basc.framework.redis.async;

/**
 * redis事务
 * 
 * @author wcnnkh
 *
 * @param <K>
 * @param <V>
 */
public interface RedisTransaction<K, V> extends AsyncRedisCommands<K, V> {
}

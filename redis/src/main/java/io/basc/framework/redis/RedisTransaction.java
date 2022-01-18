package io.basc.framework.redis;

/**
 * redis事务
 * 
 * @author wcnnkh
 *
 * @param <K>
 * @param <V>
 */
public interface RedisTransaction<K, V> extends RedisPipelineCommands<K, V> {
}

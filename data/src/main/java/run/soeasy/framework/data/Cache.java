package run.soeasy.framework.data;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 缓存接口，继承{@link Repository}接口并扩展**TTL（存活时间）管理能力**，
 * 为键值对存储提供数据自动过期特性，适配各类缓存场景（如本地内存缓存、分布式缓存Redis等）。
 * 
 * <p>核心设计思路：
 * 通过继承复用{@link Repository}的通用键值操作，同时新增带TTL参数的方法实现缓存特有能力，
 * 并将无TTL的{@link #insert(K, V)}默认实现委托给带TTL的方法，确保接口行为一致性与实现简洁性。
 * 
 * <p>TTL（存活时间）语义约定：
 * <ul>
 * <li>{@code ttl > 0}：数据在指定时间后自动过期，过期后视为不存在（查询返回空、更新/删除返回false）；</li>
 * <li>{@code ttl = 0}：表示永久有效（无过期时间），数据需通过{@link #delete(K)}手动删除；</li>
 * <li>时间单位由{@link TimeUnit}指定，实现类需保证时间转换的精度（如毫秒转秒时的取整规则）。</li>
 * </ul>
 *
 * @param <K> 键类型：缓存键，需具备唯一性和可序列化能力（分布式缓存场景），不可为null
 * @param <V> 值类型：缓存的业务实体数据（如POJO、DTO），需支持序列化（分布式缓存），不可为null
 * @see Repository 通用键值存储接口，定义基础的增删改查规范
 */
public interface Cache<K, V> extends Repository<K, V> {

    /**
     * 插入永久有效的键值对（默认实现）
     * <p>核心逻辑：通过委托给带TTL的{@link #insert(K, V, long, TimeUnit)}实现，
     * 传入{@code ttl=0}和{@link TimeUnit#MILLISECONDS}，表示数据永久有效（无自动过期）。
     * 
     * @param key   缓存键，不可为null（空值抛{@link NullPointerException}）
     * @param value 缓存值（业务实体数据），不可为null（空值抛{@link NullPointerException}）
     * @return boolean：true表示插入成功（新键添加或旧键覆盖）；false表示插入失败（如缓存容量限制）
     * @throws RuntimeException 底层缓存操作抛出的异常（如连接失败、序列化错误）
     * @see #insert(K, V, long, TimeUnit) 带过期时间的插入方法，本方法为其特殊场景（永久有效）的封装
     */
    @Override
    default boolean insert(@NonNull K key, @NonNull V value) {
        return insert(key, value, 0, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 插入键值对并指定过期时间（核心缓存操作）
     * <p>核心逻辑：将键值对存入缓存，同时设置存活时间，到期后数据自动失效。
     * 若键已存在，无论旧值是否过期，均会被新值覆盖并更新过期时间（保证幂等性）。
     * 
     * @param key       缓存键，不可为null（空值抛{@link NullPointerException}）
     * @param value     缓存值（业务实体数据），不可为null（空值抛{@link NullPointerException}）
     * @param ttl       存活时间（非负）：{@code ttl > 0}表示指定时间后过期；{@code ttl = 0}表示永久有效
     * @param timeUnit  时间单位，不可为null（空值抛{@link NullPointerException}）
     * @return boolean：true表示插入/覆盖成功；false表示插入失败（如缓存达到容量上限且无淘汰策略）
     * @throws IllegalArgumentException 若{@code ttl < 0}（负数TTL不合法）
     * @throws RuntimeException 实现类可抛出缓存相关异常（如网络超时、序列化失败），需在具体实现中说明
     */
    boolean insert(@NonNull K key, @NonNull V value, long ttl, @NonNull TimeUnit timeUnit);

    /**
     * 批量插入键值对并统一设置过期时间（默认实现）
     * <p>默认逻辑：遍历{@link Elements}中的每个键值对，调用{@link #insert(K, V, long, TimeUnit)}执行单条插入，
     * 用{@link AtomicLong}原子计数成功插入的数量（确保多线程环境下计数准确）。
     * <p>性能优化建议：底层缓存支持批量操作API时（如Redis的Pipeline），应重写本方法以减少网络交互/IO次数，
     * 默认实现仅适用于简单场景或底层不支持批量操作的缓存。
     * 
     * @param elements  待插入的键值对集合，不可为null，且集合中所有键和值均不可为null
     * @param ttl       统一的存活时间（非负），含义同{@link #insert(K, V, long, TimeUnit)}
     * @param timeUnit  时间单位，不可为null
     * @return long：成功插入的键值对数量（仅统计单条insert返回true的次数）
     * @throws IllegalArgumentException 若{@code ttl < 0}
     * @throws NullPointerException 若elements、timeUnit为null，或集合中包含null键/值
     * @throws RuntimeException 底层{@link #insert(K, V, long, TimeUnit)}抛出的异常会向上传递
     */
    default long batchInsert(@NonNull Elements<? extends KeyValue<K, V>> elements, long ttl, @NonNull TimeUnit timeUnit) {
        AtomicLong totalCount = new AtomicLong();
        CollectionUtils.acceptAll(elements, (e) -> {
            if (insert(e.getKey(), e.getValue(), ttl, timeUnit)) {
                totalCount.incrementAndGet();
            }
        });
        return totalCount.get();
    }
}
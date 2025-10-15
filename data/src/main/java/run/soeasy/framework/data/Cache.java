package run.soeasy.framework.data;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 缓存接口，继承{@link Repository}接口并扩展<strong>TTL（存活时间）管理能力</strong>，
 * 为键值对存储提供数据自动过期特性，适配各类缓存场景（如本地内存缓存、分布式缓存Redis等）。
 * 
 * <p>核心设计思路：
 * 通过继承复用{@link Repository}的通用键值操作（增删改查），同时新增带TTL参数的方法实现缓存特有能力，
 * 并将无TTL的{@link #insert}默认实现委托给带TTL的方法，确保接口行为一致性与实现简洁性。
 * 
 * <p>TTL（存活时间）语义约定（所有实现类必须遵守）：
 * <ul>
 * <li>{@code ttl > 0}：数据在指定时间后自动过期，过期后视为不存在（查询返回空集合、更新/删除返回false）；</li>
 * <li>{@code ttl = 0}：表示永久有效（无过期时间），数据需通过{@link #delete}手动删除；</li>
 * <li>{@code ttl < 0}：视为非法参数，需抛出{@link IllegalArgumentException}（由实现类校验）；</li>
 * <li>时间单位由{@link TimeUnit}指定，实现类需保证时间转换精度（如毫秒转秒时的四舍五入/截断规则需文档说明）。</li>
 * </ul>
 * 
 * <p>缓存特有约束：
 * <ul>
 * <li><strong>过期数据可见性</strong>：过期数据应被视为“不存在”，所有操作（查询/更新/删除）均应忽略过期键，避免业务读取到无效数据；</li>
 * <li><strong>覆盖策略</strong>：插入已存在的键时，无论旧值是否过期，均需用新值覆盖并更新TTL（确保新值生效）；</li>
 * <li><strong>序列化支持</strong>：分布式缓存场景下，键和值需实现序列化接口（如{@link java.io.Serializable}），具体要求由实现类文档说明。</li>
 * </ul>
 *
 * @param <K> 键类型：缓存键，需具备唯一性和可序列化能力（分布式缓存场景），不可为null
 * @param <V> 值类型：缓存的业务实体数据（如POJO、DTO），需支持序列化（分布式缓存），不可为null
 * @see Repository 通用键值存储接口，定义基础的增删改查规范
 * @see TimeUnit 时间单位枚举，用于指定TTL的单位（如毫秒、秒、分钟）
 */
public interface Cache<K, V> extends Repository<K, V> {

    /**
     * 插入永久有效的键值对（默认实现，继承自{@link Repository}）
     * <p>核心逻辑：通过委托给带TTL的{@link #insert}实现，
     * 传入{@code ttl=0}和{@link TimeUnit#MILLISECONDS}，表示数据永久有效（无自动过期）。
     * <p>与带TTL方法的关系：本方法等价于{@code insert(key, value, 0, TimeUnit.MILLISECONDS)}，
     * 所有实现类需确保两者行为一致（如覆盖策略、返回值规则）。
     * 
     * @param key   缓存键，不可为null（空值抛{@link NullPointerException}）
     * @param value 缓存值（业务实体数据），不可为null（空值抛{@link NullPointerException}）
     * @return boolean：true表示插入成功（新键添加或旧键覆盖）；false表示插入失败（如缓存容量限制、权限不足）
     * @throws RuntimeException 底层缓存操作抛出的异常（如连接失败、序列化错误、缓存服务不可用）
     * @see #insert 带过期时间的插入方法，本方法为其特殊场景（永久有效）的封装
     */
    @Override
    default boolean insert(@NonNull K key, @NonNull V value) {
        return insert(key, value, 0, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 插入键值对并指定过期时间（核心缓存操作）
     * <p>核心逻辑：将键值对存入缓存，同时设置存活时间，到期后数据自动失效（实现类需保证过期后不可访问）。
     * 若键已存在，无论旧值是否过期，均会被新值覆盖并更新过期时间（保证幂等性，重复调用效果一致）。
     * 
     * <p>典型场景：
     * <ul>
     * <li>临时数据缓存（如验证码，设置5分钟过期）；</li>
     * <li>热点数据缓存（如商品详情，设置1小时过期，定期更新）；</li>
     * <li>分布式锁实现（通过设置短TTL避免死锁）。</li>
     * </ul>
     * 
     * @param key       缓存键，不可为null（空值抛{@link NullPointerException}）
     * @param value     缓存值（业务实体数据），不可为null（空值抛{@link NullPointerException}）
     * @param ttl       存活时间（非负）：{@code ttl > 0}表示指定时间后过期；{@code ttl = 0}表示永久有效
     * @param timeUnit  时间单位，不可为null（空值抛{@link NullPointerException}）
     * @return boolean：true表示插入/覆盖成功；false表示插入失败（如缓存达到容量上限且无淘汰策略、键格式不支持）
     * @throws IllegalArgumentException 若{@code ttl < 0}（负数TTL不合法）
     * @throws RuntimeException 实现类可抛出缓存相关异常（如网络超时、序列化失败、缓存服务宕机），需在具体实现中说明
     */
    boolean insert(@NonNull K key, @NonNull V value, long ttl, @NonNull TimeUnit timeUnit);

    /**
     * 批量插入键值对并统一设置过期时间（默认实现）
     * <p>默认逻辑：遍历{@link Elements}中的每个键值对，调用{@link #insert}执行单条插入，
     * 用{@link AtomicLong}原子计数成功插入的数量（确保多线程环境下计数准确）。
     * 
     * <p>性能优化建议：
     * 底层缓存支持批量操作API时（如Redis的Pipeline、本地缓存的批量put），应重写本方法以减少网络交互/IO次数，
     * 默认实现仅适用于简单场景或底层不支持批量操作的缓存（如基础内存Map实现）。
     * 
     * @param elements  待插入的键值对集合，不可为null，且集合中所有键和值均不可为null（否则触发{@link NullPointerException}）
     * @param ttl       统一的存活时间（非负），含义同{@link #insert(Object, Object, long, TimeUnit)}
     * @param timeUnit  时间单位，不可为null（空值抛{@link NullPointerException}）
     * @return long：成功插入的键值对数量（仅统计单条insert返回true的次数）
     * @throws IllegalArgumentException 若{@code ttl < 0}
     * @throws NullPointerException 若elements、timeUnit为null，或集合中包含null键/值（由{@link #insert}的注解触发）
     * @throws RuntimeException 底层{@link #insert}抛出的异常会向上传递（如批量操作中部分失败，是否中断由实现类决定）
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

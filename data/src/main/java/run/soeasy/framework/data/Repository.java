package run.soeasy.framework.data;

import java.util.concurrent.atomic.AtomicLong;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 通用键值型数据访问接口，定义<strong>键-值映射存储</strong>的标准化操作规范，
 * 旨在统一数据库、缓存（如Redis）、键值数据库（如LevelDB）等各类存储的访问逻辑，
 * 降低多存储场景下的适配成本，同时通过强制约束（如键值非空、统一结果格式）提升代码健壮性。
 * 
 * <p>核心设计目标：
 * <ul>
 * <li><strong>接口统一</strong>：抽象“增删改查”核心操作，屏蔽不同存储的实现差异；</li>
 * <li><strong>约束强制</strong>：通过{@link NonNull}注解确保键和值不可为null，避免空指针异常；</li>
 * <li><strong>结果标准化</strong>：查询结果统一封装为{@link Elements}，无匹配时返回空集合而非null；</li>
 * <li><strong>扩展性兼容</strong>：支持自定义实现类根据存储特性调整行为，提供默认批量操作实现。</li>
 * </ul>
 *
 * <p>通用约束（所有实现类需遵守）：
 * <ul>
 * <li>键需具备<strong>唯一性</strong>：用于唯一标识数据，同一存储内不可出现“同键不同值”；</li>
 * <li>值需为<strong>业务实体数据</strong>：通常是POJO、DTO等结构化数据，避免与键的语义混淆；</li>
 * <li>操作幂等性（建议）：非插入类操作建议实现幂等，重复调用不影响最终结果。</li>
 * </ul>
 *
 * @param <K> 键类型：用于唯一标识数据，可为基础类型包装类或自定义查询条件类，<strong>不可为null</strong>
 * @param <V> 值类型：实际存储的业务实体数据，<strong>不可为null</strong>
 * @see Elements 统一的结果集合类型
 * @see KeyValue 键值对封装类
 * @see NonNull Lombok注解，强制参数非空
 */
public interface Repository<K, V> {

    /**
     * 插入单个键值对数据到存储中
     * <p>核心逻辑：通过键定位存储位置，将值与键关联存储，需处理“键已存在”的场景：
     * <ul>
     * <li>覆盖策略（如缓存）：键已存在时用新值覆盖旧值，返回true；</li>
     * <li>拒绝策略（如数据库主键）：键已存在时不执行插入，返回false（或抛异常）。</li>
     * </ul>
     *
     * @param key   唯一标识数据的键，不可为null
     * @param value 与键关联的业务实体数据，不可为null
     * @return boolean：true表示插入成功；false表示插入失败
     * @throws RuntimeException 实现类可抛出存储相关异常
     */
    boolean insert(@NonNull K key, @NonNull V value);

    /**
     * 批量插入键值对数据（默认实现）
     * <p>默认逻辑：遍历集合中的每个键值对，调用{@link #insert}执行单条插入，
     * 用原子变量计数成功插入的数量。
     * <p>扩展建议：复杂场景可重写此方法，使用底层存储的批量API提升性能。
     *
     * @param elements 待插入的键值对集合，不可为null
     * @return long：成功插入的键值对数量
     * @throws NullPointerException 若elements为null或包含null键/值
     * @throws RuntimeException     底层{@link #insert}抛出的异常会向上传递
     */
    default long batchInsert(@NonNull Elements<? extends KeyValue<K, V>> elements) {
        AtomicLong totalCount = new AtomicLong();
        CollectionUtils.acceptAll(elements, (e) -> {
            if (insert(e.getKey(), e.getValue())) {
                totalCount.incrementAndGet();
            }
        });
        return totalCount.get();
    }

    /**
     * 根据键删除存储中的单个数据
     * <p>核心逻辑：仅删除与指定键<strong>严格匹配</strong>的数据，不影响其他键值对；
     * 若键不存在，返回false（无副作用）。
     *
     * @param key 待删除数据的键，不可为null
     * @return boolean：true表示删除成功；false表示删除失败
     * @throws RuntimeException 实现类可抛出存储相关异常
     */
    boolean delete(@NonNull K key);

    /**
     * 批量删除数据（默认实现）
     * <p>默认逻辑：遍历键集合，调用{@link #delete}执行单条删除，
     * 用原子变量计数成功删除的数量。
     * <p>扩展建议：底层支持批量删除API时，重写此方法以提升性能。
     *
     * @param keys 待删除的键集合，不可为null
     * @return long：成功删除的键数量
     * @throws NullPointerException 若keys为null或包含null键
     * @throws RuntimeException     底层{@link #delete}抛出的异常会向上传递
     */
    default long batchDelete(@NonNull Elements<? extends K> keys) {
        AtomicLong totalCount = new AtomicLong();
        CollectionUtils.acceptAll(keys, (e) -> {
            if (delete(e)) {
                totalCount.incrementAndGet();
            }
        });
        return totalCount.get();
    }

    /**
     * 根据键更新存储中的单个数据
     * <p>核心逻辑：仅当键已存在时执行更新，用新值替换旧值；
     * 若键不存在，不执行任何操作并返回false。
     *
     * @param key   待更新数据的键，不可为null
     * @param value 替换后的新业务实体数据，不可为null
     * @return boolean：true表示更新成功；false表示更新失败
     * @throws RuntimeException 实现类可抛出存储相关异常
     */
    boolean update(@NonNull K key, @NonNull V value);

    /**
     * 批量更新数据（默认实现）
     * <p>默认逻辑：遍历键值对集合，调用{@link #update}执行单条更新，
     * 用原子变量计数成功更新的数量。
     * <p>扩展建议：底层支持批量更新API时，重写此方法以提升性能。
     *
     * @param elements 待更新的键值对集合，不可为null
     * @return long：成功更新的键值对数量
     * @throws NullPointerException 若elements为null或包含null键/值
     * @throws RuntimeException     底层{@link #update}抛出的异常会向上传递
     */
    default long batchUpdate(@NonNull Elements<? extends KeyValue<K, V>> elements) {
        AtomicLong totalCount = new AtomicLong();
        CollectionUtils.acceptAll(elements, (e) -> {
            if (update(e.getKey(), e.getValue())) {
                totalCount.incrementAndGet();
            }
        });
        return totalCount.get();
    }

    /**
     * 根据键查询匹配的键值对数据（核心查询操作）
     * <p>核心逻辑：以键作为查询条件，返回所有匹配的键值对，匹配规则由实现类定义：
     * <ul>
     * <li>精确匹配：键为唯一标识，返回0或1个键值对；</li>
     * <li>模糊匹配：键为含通配符的条件，返回多个匹配结果；</li>
     * <li>范围匹配：键为范围条件，返回范围内的结果。</li>
     * </ul>
     * <p>结果约定：无论是否有匹配数据，均返回非null的{@link Elements}，无匹配时返回空集合。
     *
     * @param key 查询条件键，不可为null
     * @return 匹配的键值对集合，无匹配时返回空集合（非null）
     * @throws RuntimeException 实现类可抛出存储相关异常
     * @see Elements#isEmpty() 判断查询结果是否为空
     */
    Elements<KeyValue<K, V>> query(@NonNull K key);

    /**
     * 批量查询多个键对应的键值对数据（默认实现）
     * <p>核心逻辑：遍历待查询键集合，对每个键调用{@link #query}执行单键查询，
     * 合并所有结果为一个统一集合。
     * <p>扩展建议：底层支持批量查询API时，重写此方法以提升性能。
     *
     * @param keys 待查询的键集合，不可为null
     * @return 所有键的匹配结果聚合集合，无匹配时返回空集合（非null）
     * @throws NullPointerException 若keys为null或包含null键
     * @throws RuntimeException     底层{@link #query}抛出的异常会向上传递
     * @see #query 单键查询方法
     */
    default Elements<KeyValue<K, V>> batchQuery(@NonNull Elements<? extends K> keys) {
        return keys.flatMap(this::query);
    }
}

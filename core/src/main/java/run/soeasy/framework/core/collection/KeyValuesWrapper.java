package run.soeasy.framework.core.collection;

/**
 * 键值对包装器接口，用于对键值对集合进行统一封装和操作委托。
 * 该接口继承自KeyValues和KeysWrapper接口，
 * 提供了对底层键值对集合的透明包装，所有操作默认委托给被包装的源对象。
 *
 * <p>设计特点：
 * <ul>
 *   <li>通过包装模式实现对KeyValues实例的功能增强和操作代理</li>
 *   <li>所有方法默认委派给被包装的源KeyValues实现</li>
 *   <li>保持与原始KeyValues完全一致的行为和语义</li>
 *   <li>类型安全的泛型设计，确保包装器与被包装类型的一致性</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要对现有KeyValues添加额外功能（如日志记录、权限控制）</li>
 *   <li>需要统一处理不同类型的KeyValues实现</li>
 *   <li>需要在不修改原始实现的情况下添加自定义行为</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <W> 被包装的键值对类型，必须实现KeyValues接口
 * @see KeyValues
 * @see KeysWrapper
 */
public interface KeyValuesWrapper<K, V, W extends KeyValues<K, V>> extends KeyValues<K, V>, KeysWrapper<K, W> {

    /**
     * 获取指定键对应的所有值的集合。
     * 该方法默认委派给被包装的源KeyValues实现。
     *
     * @param key 要查询的键
     * @return 包含该键对应所有值的Elements集合
     */
    @Override
    default Elements<V> getValues(K key) {
        return getSource().getValues(key);
    }
}
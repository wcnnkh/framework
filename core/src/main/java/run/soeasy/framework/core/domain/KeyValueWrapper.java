package run.soeasy.framework.core.domain;

/**
 * 键值对包装器接口，用于包装{@link KeyValue}实例并委托所有操作，
 * 实现装饰器模式以支持对键值对操作的透明增强。该接口继承自{@link KeyValue}和{@link Wrapper}，
 * 允许在不修改原始键值对的前提下添加额外功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明委托：所有键值访问方法均转发给被包装的{@link KeyValue}实例</li>
 *   <li>装饰扩展：支持通过包装器添加日志记录、验证、缓存等额外功能</li>
 *   <li>类型安全：通过泛型确保包装器与被包装键值对的类型一致性</li>
 *   <li>函数式支持：作为函数式接口，可通过lambda表达式创建轻量级包装器</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>键值对访问日志记录：记录所有键值获取操作的访问日志</li>
 *   <li>值验证增强：在获取值前进行合法性验证</li>
 *   <li>键值对缓存：缓存频繁访问的键值对数据</li>
 *   <li>事务性键值操作：为键值对操作添加事务边界</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 原始键值对
 * KeyValue&lt;String, Integer&gt; original = KeyValue.of("age", 25);
 * 
 * // 包装键值对并添加日志记录
 * KeyValueWrapper&lt;String, Integer, KeyValue&lt;String, Integer&gt;&gt; logged = value -&gt; {
 *     System.out.println("Access key: " + value.getKey());
 *     return original;
 * };
 * 
 * // 访问包装后的键值对
 * int age = logged.getValue(); // 访问时打印日志
 * </pre>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <W> 被包装的键值对类型，必须是{@link KeyValue}的子类型
 * @see KeyValue
 * @see Wrapper
 */
public interface KeyValueWrapper<K, V, W extends KeyValue<K, V>> extends KeyValue<K, V>, Wrapper<W> {
    
    /**
     * 获取键值对的键，转发给被包装的KeyValue实例。
     *
     * @return 被包装键值对的键
     * @see KeyValue#getKey()
     */
    @Override
    default K getKey() {
        return getSource().getKey();
    }
    
    /**
     * 获取键值对的值，转发给被包装的KeyValue实例。
     *
     * @return 被包装键值对的值
     * @see KeyValue#getValue()
     */
    @Override
    default V getValue() {
        return getSource().getValue();
    }
}
package run.soeasy.framework.core.concurrent;

import java.io.Serializable;
import java.util.Map.Entry;

import lombok.Data;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 只读键值对，实现不可变的Map.Entry和KeyValue接口。
 * 该类表示一个不可变的键值对，创建后无法修改其键和值，
 * 适合用于需要防止数据被修改的场景，如配置信息传递、
 * 不可变数据结构构建等。
 *
 * <p>核心特性：
 * <ul>
 *   <li>键和值在构造时初始化，之后不可修改</li>
 *   <li>实现Serializable接口，支持对象序列化</li>
 *   <li>重写setValue方法并抛出UnsupportedOperationException</li>
 *   <li>提供equals/hashCode/toString方法的自动实现</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>配置信息的安全传递</li>
 *   <li>不可变Map的条目</li>
 *   <li>需要防止数据被修改的API返回值</li>
 *   <li>多线程环境下无需同步的只读数据</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @see Entry
 * @see KeyValue
 * @see Serializable
 */
@Data
public class ReadOnlyEntry<K, V> implements Entry<K, V>, KeyValue<K, V>, Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 不可变的键，在构造时初始化。
     */
    private final K key;
    
    /**
     * 不可变的值，在构造时初始化。
     */
    private final V value;

    /**
     * 禁止修改值的操作，抛出UnsupportedOperationException。
     * 该方法实现了Map.Entry接口的setValue方法，但由于该类是只读的，
     * 任何调用该方法的行为都会导致异常。
     *
     * @param value 新值（被忽略）
     * @throws UnsupportedOperationException 总是抛出此异常
     */
    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException("readOnly");
    }
    
    /**
     * 提供更友好的字符串表示形式。
     * 输出格式为："key=value"。
     *
     * @return 键值对的字符串表示
     */
    @Override
    public String toString() {
        return key + "=" + value;
    }
}
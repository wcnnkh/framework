package run.soeasy.framework.core.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * MultiValueMap接口的抽象实现，提供了基本的多值映射功能。
 * 该类实现了MapWrapper接口，允许包装一个底层Map来存储多值映射关系。
 * 
 * @author soeasy.run
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <M> 底层Map的类型，必须是Map&lt;K, List&lt;V&gt;&gt;的子类型
 */
@Getter
@Setter
public abstract class AbstractMultiValueMap<K, V, M extends Map<K, List<V>>>
        implements MultiValueMap<K, V>, MapWrapper<K, List<V>, M> {
    
    /**
     * 用于创建新值列表的函数。当键不存在时，会使用此函数创建一个新的列表。
     * 默认创建ArrayList实例。
     */
    @NonNull
    private Function<? super K, ? extends List<V>> valuesCreator = (e) -> new ArrayList<>();

    /**
     * 向指定键的值列表添加多个值。
     * 如果键不存在，会使用valuesCreator创建一个新的列表并添加这些值。
     * 
     * @param key 键对象
     * @param values 要添加的值列表
     */
    @Override
    public void adds(K key, List<V> values) {
        List<V> list = getSource().get(key);
        if (list == null) {
            list = valuesCreator.apply(key);
            getSource().put(key, list);
        }
        list.addAll(values);
    }

    /**
     * 设置指定键的单个值。
     * 此操作会创建一个新的列表并只包含该值，覆盖键原有的所有值。
     * 
     * @param key 键对象
     * @param value 要设置的值
     */
    @Override
    public void set(K key, V value) {
        List<V> list = valuesCreator.apply(key);
        list.add(value);
        put(key, list);
    }

    /**
     * 返回底层Map的字符串表示形式。
     * 
     * @return 底层Map的字符串表示
     */
    @Override
    public String toString() {
        return getSource().toString();
    }

    /**
     * 返回底层Map的哈希码值。
     * 
     * @return 底层Map的哈希码
     */
    @Override
    public int hashCode() {
        return getSource().hashCode();
    }

    /**
     * 比较此MultiValueMap与指定对象是否相等。
     * 如果指定对象也是AbstractMultiValueMap，则比较它们的底层Map；
     * 否则直接比较底层Map与指定对象。
     * 
     * @param obj 要比较的对象
     * @return 如果相等则返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof AbstractMultiValueMap) {
            return getSource().equals((((AbstractMultiValueMap<?, ?, ?>) obj).getSource()));
        }
        return getSource().equals(obj);
    }
}
package run.soeasy.framework.core.collection;

import run.soeasy.framework.core.domain.Wrapper;

/**
 * 键集合包装器接口，继承自Keys和Wrapper，用于封装具有键集合特性的对象。
 * 实现此接口的类可对Keys类型的对象进行包装，提供代理访问和增强功能。
 *
 * @author soeasy.run
 * @param <K> 键类型
 * @param <W> 被包装的Keys类型
 * @see Keys
 * @see Wrapper
 */
public interface KeysWrapper<K, W extends Keys<K>> extends Keys<K>, Wrapper<W> {
    
    /**
     * 获取被包装对象的键集合。
     * 该方法代理调用源Keys对象的keys()方法，返回其键集合。
     *
     * @return 被包装对象的键集合
     */
    @Override
    default Elements<K> keys() {
        return getSource().keys();
    }

    /**
     * 判断被包装对象是否包含指定键。
     * 该方法代理调用源Keys对象的hasKey()方法，返回其判断结果。
     *
     * @param key 待检查的键，不可为null
     * @return 如果包含指定键，返回true；否则返回false
     */
    @Override
    default boolean hasKey(K key) {
        return getSource().hasKey(key);
    }
}
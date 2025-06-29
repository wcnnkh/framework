package run.soeasy.framework.core.collection;

/**
 * 键集合接口
 * 定义获取键集合和检查键存在性的标准行为
 * 
 * @author soeasy.run
 *
 * @param <K> 键的类型
 */
public interface Keys<K> {
    /**
     * 获取所有键的集合
     * 
     * @return 键的元素集合
     */
    Elements<K> keys();

    /**
     * 判断是否存在指定键
     * 
     * @param key 待检查的键
     * @return 存在返回true，否则返回false
     */
    default boolean hasKey(K key) {
        return keys().contains(key);
    }
}
package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 列表工厂接口，继承自CollectionFactory，专门用于创建、展示和克隆列表实例。
 * 该接口优化了集合展示逻辑，针对List类型的源集合提供更高效的只读视图转换。
 *
 * @author soeasy.run
 * @param <E> 列表元素类型
 * @param <T> 具体列表实现类型
 * @see CollectionFactory
 */
public interface ListFactory<E, T extends List<E>> extends CollectionFactory<E, T> {
    
    /**
     * 将源集合转换为只读形式的列表，用于安全地对外展示。
     * 如果源集合本身是List类型，则直接返回其不可修改视图；
     * 否则调用clone方法创建一个新的列表并返回其不可修改视图。
     * 此方法优化了List类型的处理，避免不必要的克隆操作。
     *
     * @param source 源集合，不可为null
     * @return 只读的列表视图
     */
    @Override
    default List<E> display(Collection<E> source) {
        return (source instanceof List) ? 
            Collections.unmodifiableList((List<E>) source) : 
            clone(source);
    }
}
package run.soeasy.framework.core.collection;

import run.soeasy.framework.core.domain.Wrapper;

/**
 * 列表化包装器接口，继承自Listable和Wrapper，用于封装列表化元素集合。
 * 实现此接口的类可对Listable类型的元素集合进行包装，提供代理访问和增强功能。
 *
 * @author soeasy.run
 * @param <E> 元素类型
 * @param <W> 被包装的Listable类型
 * @see Listable
 * @see Wrapper
 */
public interface ListableWrapper<E, W extends Listable<E>> extends Listable<E>, Wrapper<W> {
    
    /**
     * 获取被包装的元素集合。
     * 该方法代理调用源Listable的getElements()方法，返回其元素集合。
     *
     * @return 被包装的元素集合实例
     */
    @Override
    default Elements<E> getElements() {
        return getSource().getElements();
    }

    /**
     * 判断被包装的元素集合是否包含元素。
     * 该方法代理调用源Listable的hasElements()方法，返回其判断结果。
     *
     * @return true表示元素集合为空，false表示包含元素
     */
    @Override
    default boolean hasElements() {
        return getSource().hasElements();
    }
}
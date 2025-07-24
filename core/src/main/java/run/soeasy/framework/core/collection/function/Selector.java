package run.soeasy.framework.core.collection.function;

import run.soeasy.framework.core.collection.Elements;

/**
 * 元素选择器接口，用于从元素集合中选择单个元素。
 * 该接口扩展自Filter接口，允许将选择器作为特殊的过滤器使用，
 * 选择结果会被包装成包含零个或一个元素的Elements集合。
 *
 * @author soeasy.run
 * @param <T> 元素类型
 * @see Filter
 * @see Elements
 */
public interface Selector<T> extends Filter<T> {
    
    /**
     * 从元素集合中选择单个元素。
     * 具体选择逻辑由实现类决定，可能是第一个元素、最后一个元素、
     * 满足特定条件的元素或随机元素等。
     *
     * @param elements 元素集合，不可为null
     * @return 选择的单个元素，可能为null（如果没有合适的元素）
     */
    T select(Elements<T> elements);

    /**
     * 将选择器转换为过滤器，返回包含单个选择元素的Elements集合。
     * 该方法实现了Filter接口的apply方法，将select方法的结果包装为Elements集合：
     * - 如果选择的元素为null，返回空的Elements集合
     * - 否则返回只包含该元素的Elements集合
     *
     * @param elements 元素集合，不可为null
     * @return 包含零个或一个元素的Elements集合
     */
    @Override
    default Elements<T> apply(Elements<T> elements) {
        T singleton = select(elements);
        return singleton == null ? Elements.empty() : Elements.singleton(singleton);
    }
}
package run.soeasy.framework.core.collection;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 空提供者的具体实现，继承自EmptyElements并实现Provider接口。
 * 该类采用单例模式，提供表示无元素提供能力的统一实例，用于简化空数据场景的处理逻辑。
 * 
 * @author soeasy.run
 * @param <S> 提供的元素类型（实际无元素）
 * @see Provider
 * @see EmptyElements
 */
public class EmptyProvider<S> extends EmptyElements<S> implements Provider<S> {
    private static final long serialVersionUID = 1L;
    
    /**
     * 空提供者的单例实例，用于表示不提供任何元素的场景。
     * 该实例为类型安全的泛型对象，推荐直接使用此实例以避免重复创建空提供者对象。
     */
    static final Provider<?> EMPTY_PROVIDER = new EmptyProvider<>();

    /**
     * 重载提供者的元素数据，空提供者无实际元素，此方法不执行任何操作。
     * 实现{@link Reloadable}接口的空方法，确保接口兼容性。
     */
    @Override
    public void reload() {
        // 空实现，无元素需要重载
    }

    /**
     * 对空提供者执行过滤操作，由于无元素存在，过滤结果仍为此空提供者。
     * 
     * @param predicate 过滤条件（该参数实际不会被使用）
     * @return 当前空提供者实例
     */
    @Override
    public Provider<S> filter(Predicate<? super S> predicate) {
        return this;
    }

    /**
     * 对空提供者的元素执行映射操作，由于无元素存在，返回新的空提供者。
     * 
     * @param mapper 元素映射函数（该参数实际不会被使用）
     * @param <U> 映射后的元素类型
     * @return 新的空Provider实例
     */
    @Override
    public <U> Provider<U> map(Function<? super S, ? extends U> mapper) {
        return Provider.empty();
    }
}
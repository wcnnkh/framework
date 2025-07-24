package run.soeasy.framework.core.collection.function;

import java.util.function.Function;
import java.util.function.Predicate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;

/**
 * 元素过滤器函数式接口，用于对Elements集合执行过滤操作。
 * 该接口扩展了Java Function接口，接收一个Elements集合并返回过滤后的Elements集合。
 * 提供了基于Predicate的过滤器实现和常用的静态工厂方法。
 *
 * @author soeasy.run
 * @param <T> 元素类型
 * @see Function
 * @see Elements
 */
@FunctionalInterface
public interface Filter<T> extends Function<Elements<T>, Elements<T>> {
    
    /**
     * 基于Predicate实现的过滤器。
     * 该类将Predicate应用于Elements的filter方法，实现元素过滤功能。
     */
    @RequiredArgsConstructor
    public static class PredicateFilter<T> implements Filter<T> {
        /**
         * 忽略null元素的过滤器实例，用于快速获取常见的null过滤功能。
         */
        private static final Filter<?> IGNORE_NULL = new PredicateFilter<>((e) -> e != null);

        /**
         * 过滤条件，不可为null。
         */
        @NonNull
        private final Predicate<? super T> predicate;

        /**
         * 对Elements集合应用过滤条件。
         * 该方法调用Elements的filter方法，并传入Predicate进行元素过滤。
         *
         * @param elements 待过滤的元素集合，不可为null
         * @return 过滤后的元素集合
         */
        @Override
        public Elements<T> apply(@NonNull Elements<T> elements) {
            return elements.filter(predicate);
        }
    }

    /**
     * 创建基于Predicate的过滤器。
     * 该方法返回一个PredicateFilter实例，使用指定的Predicate进行元素过滤。
     *
     * @param <T>       元素类型
     * @param predicate 过滤条件，不可为null
     * @return 基于Predicate的过滤器
     */
    public static <T> Filter<T> forPredicate(@NonNull Predicate<? super T> predicate) {
        return new PredicateFilter<>(predicate);
    }

    /**
     * 返回恒等过滤器，不执行任何过滤操作，直接返回原集合。
     * 该方法通常用于空过滤或默认过滤场景。
     *
     * @param <T> 元素类型
     * @return 恒等过滤器
     */
    static <T> Filter<T> identity() {
        return t -> t;
    }

    /**
     * 返回忽略null元素的过滤器。
     * 该过滤器使用(e -> e != null)的Predicate，过滤掉集合中的所有null元素。
     *
     * @param <T> 元素类型
     * @return 忽略null元素的过滤器
     */
    @SuppressWarnings("unchecked")
    public static <T> Filter<T> ignoreNull() {
        return (Filter<T>) PredicateFilter.IGNORE_NULL;
    }

    /**
     * 对Elements集合应用过滤操作。
     * 该方法是Filter接口的函数式方法，由具体实现类实现过滤逻辑。
     *
     * @param elements 待过滤的元素集合
     * @return 过滤后的元素集合
     */
    @Override
    Elements<T> apply(Elements<T> elements);
}
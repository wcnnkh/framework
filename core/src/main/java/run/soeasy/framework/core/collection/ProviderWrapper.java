package run.soeasy.framework.core.collection;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import lombok.NonNull;

/**
 * 提供者包装器接口，用于对Provider实例进行统一封装和操作委托。
 * 该接口继承自Provider和ElementsWrapper，提供了对底层Provider的透明包装，
 * 所有操作默认委托给被包装的源Provider对象，支持函数式编程风格的操作链。
 *
 * <p>设计特点：
 * <ul>
 *   <li>函数式接口设计：可作为lambda表达式或方法引用的目标类型</li>
 *   <li>包装模式实现：通过getSource()获取被包装的源Provider实例</li>
 *   <li>操作委托机制：所有方法默认委派给源Provider的对应实现</li>
 *   <li>类型安全的泛型设计：确保包装器与被包装类型的一致性</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要为Provider添加额外功能（如日志记录、权限校验）</li>
 *   <li>需要统一处理不同类型的Provider实现</li>
 *   <li>需要通过函数式操作链组合多个Provider</li>
 *   <li>需要在不修改原始实现的情况下扩展Provider功能</li>
 * </ul>
 *
 * @param <S> 元素类型
 * @param <W> 被包装的Provider类型，必须实现Provider接口
 * @see Provider
 * @see ElementsWrapper
 */
@FunctionalInterface
public interface ProviderWrapper<S, W extends Provider<S>> extends Provider<S>, ElementsWrapper<S, W> {

    /**
     * 重新加载数据，委托给源Provider的reload方法。
     * 该方法会触发源Provider的数据刷新逻辑。
     */
    @Override
    default void reload() {
        getSource().reload();
    }

    /**
     * 返回元素的流，委托给源Provider的stream方法。
     *
     * @return 元素的Stream实例
     */
    @Override
    default Stream<S> stream() {
        return getSource().stream();
    }

    /**
     * 对元素流进行转换，委托给源Provider的map方法。
     *
     * @param resize     是否调整结果流的大小
     * @param converter  流转换函数，将源流转换为目标流
     * @param <U>        转换后的元素类型
     * @return 转换后的Provider实例
     */
    @Override
    default <U> Provider<U> map(boolean resize, Function<? super Stream<S>, ? extends Stream<U>> converter) {
        return getSource().map(resize, converter);
    }

    /**
     * 连接元素集合，委托给源Provider的concat方法。
     *
     * @param elements 要连接的元素集合
     * @return 连接后的Provider实例
     */
    @Override
    default Provider<S> concat(Elements<? extends S> elements) {
        return getSource().concat(elements);
    }

    /**
     * 连接另一个Provider，委托给源Provider的concat方法。
     *
     * @param serviceLoader 要连接的Provider实例
     * @return 连接后的Provider实例
     */
    @Override
    default Provider<S> concat(Provider<? extends S> serviceLoader) {
        return getSource().concat(serviceLoader);
    }

    /**
     * 创建已知大小的Provider，委托给源Provider的knownSize方法。
     *
     * @param statisticsSize 大小统计函数
     * @return 已知大小的Provider实例
     */
    @Override
    default Provider<S> knownSize(@NonNull ToLongFunction<? super Elements<S>> statisticsSize) {
        return getSource().knownSize(statisticsSize);
    }

    /**
     * 过滤元素，委托给源Provider的filter方法。
     *
     * @param predicate 过滤谓词
     * @return 过滤后的Provider实例
     */
    @Override
    default Provider<S> filter(@NonNull Predicate<? super S> predicate) {
        return getSource().filter(predicate);
    }

    /**
     * 映射元素，委托给源Provider的map方法。
     *
     * @param mapper 映射函数
     * @param <U>    映射后的元素类型
     * @return 映射后的Provider实例
     */
    @Override
    default <U> Provider<U> map(Function<? super S, ? extends U> mapper) {
        return getSource().map(mapper);
    }
}
package run.soeasy.framework.core.spi;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

import run.soeasy.framework.core.collection.Reloadable;
import run.soeasy.framework.core.exchange.Registrations;

/**
 * 服务包含集合接口，集成注册管理与服务聚合能力，支持批量操作服务包含实例。
 * <p>
 * 该函数式接口继承自{@link Registrations}和{@link Include}，既可以管理多个{@link Include}实例的生命周期，
 * 又能将这些实例的服务内容聚合为统一的访问接口，适用于需要批量处理服务集合的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>注册管理：继承{@link Registrations}，支持批量注册、注销和状态查询</li>
 *   <li>服务聚合：继承{@link Include}，将多个{@link Include}的服务实例扁平化为统一流</li>
 *   <li>批量重新加载：通过{@link #reload}方法触发所有包含实例的重新加载</li>
 *   <li>函数式支持：作为函数式接口，支持通过lambda表达式实现自定义聚合逻辑</li>
 * </ul>
 *
 * @param <S> 服务实例的类型
 * @param <I> 包含的{@link Include}实现类型
 * 
 * @author soeasy.run
 * @see Registrations
 * @see Include
 */
@FunctionalInterface
public interface Includes<S, I extends Include<S>> extends Registrations<I>, Include<S> {
    
    /**
     * 重新加载所有包含的服务实例
     * <p>
     * 遍历所有{@link Include}实例并调用其{@link Reloadable#reload}方法，
     * 实现服务实例的批量更新，适用于动态服务发现场景。
     */
    @Override
    default void reload() {
        getElements().forEach(Reloadable::reload);
    }
    
    /**
     * 获取所有服务实例的流式视图
     * <p>
     * 将所有{@link Include}实例的服务流扁平化为单一流，
     * 等价于{@code getElements().flatMap(Function.identity()).stream()}
     * 
     * @return 服务实例流
     */
    @Override
    default Stream<S> stream() {
        return getElements().flatMap(Function.identity()).stream();
    }
    
    /**
     * 获取所有服务实例的迭代器
     * <p>
     * 将所有{@link Include}实例的迭代器合并为单一迭代器，
     * 等价于{@code getElements().flatMap(Function.identity()).iterator()}
     * 
     * @return 服务实例迭代器
     */
    @Override
    default Iterator<S> iterator() {
        return getElements().flatMap(Function.identity()).iterator();
    }
    
    /**
     * 判断是否包含服务实例
     * <p>
     * 委托给{@link Registrations#hasElements}实现，
     * 检查是否存在至少一个非空的{@link Include}实例
     * 
     * @return true表示包含服务实例，false表示不包含
     */
    @Override
    default boolean hasElements() {
        return Registrations.super.hasElements();
    }
}
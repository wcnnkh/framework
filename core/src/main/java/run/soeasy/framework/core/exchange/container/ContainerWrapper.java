package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Receipt;

/**
 * 容器包装器接口，定义对基础容器的装饰器模式实现。
 * 该接口继承自{@link Container}和{@link RegistryWrapper}，
 * 允许在不修改原有容器的情况下增强其注册、注销和管理功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明代理：默认实现将所有操作委派给源容器</li>
 *   <li>功能增强：子类可选择性覆盖方法以添加额外行为</li>
 *   <li>生命周期管理：支持容器的重置和取消操作</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>添加元素注册/注销的拦截逻辑</li>
 *   <li>实现容器操作的事务性保障</li>
 *   <li>添加容器状态的监控和统计功能</li>
 *   <li>实现容器的条件性访问控制</li>
 * </ul>
 *
 * @param <E> 容器中元素的类型
 * @param <R> 容器使用的注册类型，需实现{@link PayloadRegistration}
 * @param <W> 被包装的基础容器类型
 * 
 * @author soeasy.run
 * @see Container
 * @see RegistryWrapper
 * @see PayloadRegistration
 * @see Receipt
 */
public interface ContainerWrapper<E, R extends PayloadRegistration<E>, W extends Container<E, R>>
        extends Container<E, R>, RegistryWrapper<E, W> {

    /**
     * 判断容器是否为空
     * 默认实现将操作委派给源容器
     * 
     * @return 如果容器为空返回true，否则返回false
     */
    @Override
    default boolean isEmpty() {
        return getSource().isEmpty();
    }

    /**
     * 批量注销容器中的元素
     * 默认实现将操作委派给源容器
     * 
     * @param elements 待注销的元素集合
     * @return 注销操作的回执，包含操作结果信息
     */
    @Override
    default Receipt deregisters(Elements<? extends E> elements) {
        return getSource().deregisters(elements);
    }

    /**
     * 注销容器中的单个元素
     * 默认实现将操作委派给源容器
     * 
     * @param element 待注销的元素
     * @return 注销操作的回执，包含操作结果信息
     */
    @Override
    default Receipt deregister(E element) {
        return getSource().deregister(element);
    }

    /**
     * 重置容器状态
     * 默认实现将操作委派给源容器
     * 
     * <p>重置操作通常会清除容器中的所有元素，
     * 并将容器状态恢复到初始状态。
     */
    @Override
    default void reset() {
        getSource().reset();
    }

    /**
     * 判断容器是否已被取消
     * 默认实现将操作委派给源容器
     * 
     * @return 如果容器已被取消返回true，否则返回false
     */
    @Override
    default boolean isCancelled() {
        return getSource().isCancelled();
    }

    /**
     * 判断容器是否可以被取消
     * 默认实现将操作委派给源容器
     * 
     * @return 如果容器可以被取消返回true，否则返回false
     */
    @Override
    default boolean isCancellable() {
        return getSource().isCancellable();
    }

    /**
     * 尝试取消容器
     * 默认实现将操作委派给源容器
     * 
     * @return 如果取消操作成功返回true，否则返回false
     */
    @Override
    default boolean cancel() {
        return getSource().cancel();
    }
}
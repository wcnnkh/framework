package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.ElementsWrapper;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 注册表包装器接口，定义对基础注册表的装饰器模式实现。
 * 该接口继承自{@link Registry}和{@link ElementsWrapper}，
 * 允许在不修改原有注册表的情况下增强其功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明代理：默认实现将所有注册操作委派给源注册表</li>
 *   <li>功能增强：子类可选择性覆盖方法以添加额外行为</li>
 *   <li>类型安全：通过泛型参数确保元素类型一致性</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>添加注册元素的验证逻辑</li>
 *   <li>实现注册操作的事务管理</li>
 *   <li>添加注册元素的缓存或监控功能</li>
 *   <li>实现注册表的只读视图</li>
 * </ul>
 *
 * @param <E> 注册表中元素的类型
 * @param <W> 被包装的基础注册表类型
 * 
 * @author soeasy.run
 * @see Registry
 * @see ElementsWrapper
 * @see Registration
 */
@FunctionalInterface
public interface RegistryWrapper<E, W extends Registry<E>> extends Registry<E>, ElementsWrapper<E, W> {

    /**
     * 批量注册元素到注册表
     * 默认实现将操作委派给源注册表
     * 
     * @param elements 待注册的元素集合
     * @return 注册操作的句柄，用于后续管理
     * @throws RegistrationException 注册失败时抛出
     */
    @Override
    default Registration registers(Elements<? extends E> elements) throws RegistrationException {
        return getSource().registers(elements);
    }

    /**
     * 注册单个元素到注册表
     * 默认实现将操作委派给源注册表
     * 
     * @param element 待注册的元素
     * @return 注册操作的句柄，用于后续管理
     * @throws RegistrationException 注册失败时抛出
     */
    @Override
    default Registration register(E element) throws RegistrationException {
        return getSource().register(element);
    }
}
package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.exchange.Registration;

/**
 * 元素注册包装器接口，用于透明包装{@link ElementRegistration}实例。
 * <p>
 * 该接口继承自{@link ElementRegistration}、{@link PayloadRegistrationWrapper}和{@link LifecycleRegistrationWrapper}，
 * 提供对源注册对象的方法转发实现，确保包装器与源注册的行为一致性。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>透明转发：所有方法调用自动转发至被包装的源注册对象</li>
 *   <li>类型安全：通过泛型约束确保包装器与源注册的类型一致性</li>
 *   <li>生命周期同步：自动同步源注册的生命周期状态</li>
 * </ul>
 *
 * @param <E> 元素载荷类型
 * @param <W> 被包装的源注册类型，需继承{@link ElementRegistration}<{@link E}>
 * 
 * @author shuchaowen
 * @see ElementRegistration
 * @see PayloadRegistrationWrapper
 * @see LifecycleRegistrationWrapper
 */
@FunctionalInterface
public interface ElementRegistrationWrapper<E, W extends ElementRegistration<E>>
        extends ElementRegistration<E>, PayloadRegistrationWrapper<E, W>, LifecycleRegistrationWrapper<W> {

    /**
     * 获取注册元素的载荷数据
     * <p>
     * 该方法将调用转发至被包装的源注册对象的{@link ElementRegistration#getPayload()}方法。
     * 
     * @return 注册元素的载荷数据
     * @see ElementRegistration#getPayload()
     */
    @Override
    default E getPayload() {
        return getSource().getPayload();
    }

    /**
     * 组合当前注册与另一个注册
     * <p>
     * 该方法将调用转发至{@link ElementRegistration#and(Registration)}的默认实现，
     * 最终转发至被包装的源注册对象的{@link ElementRegistration#and(Registration)}方法。
     * 
     * @param registration 要组合的注册对象
     * @return 组合后的元素注册对象
     * @see ElementRegistration#and(Registration)
     */
    @Override
    default ElementRegistration<E> and(Registration registration) {
        return ElementRegistration.super.and(registration);
    }

    /**
     * 设置注册元素的载荷数据并返回旧值
     * <p>
     * 该方法将调用转发至被包装的源注册对象的{@link ElementRegistration#setPayload(Object)}方法。
     * 
     * @param payload 新的载荷数据
     * @return 旧的载荷数据
     * @see ElementRegistration#setPayload(Object)
     */
    @Override
    default E setPayload(E payload) {
        return getSource().setPayload(payload);
    }

    /**
     * 启动注册对象的生命周期
     * <p>
     * 该方法将调用转发至被包装的源注册对象的{@link LifecycleRegistration#start()}方法。
     * 调用后{@link #isRunning()}应返回true。
     * 
     * @see LifecycleRegistration#start()
     */
    @Override
    default void start() {
        getSource().start();
    }

    /**
     * 停止注册对象的生命周期
     * <p>
     * 该方法将调用转发至被包装的源注册对象的{@link LifecycleRegistration#stop()}方法。
     * 调用后{@link #isRunning()}应返回false。
     * 
     * @see LifecycleRegistration#stop()
     */
    @Override
    default void stop() {
        getSource().stop();
    }

    /**
     * 检查注册对象的生命周期是否处于运行状态
     * <p>
     * 该方法将调用转发至被包装的源注册对象的{@link LifecycleRegistration#isRunning()}方法。
     * 
     * @return true表示已启动且未停止，false表示未启动或已停止
     * @see LifecycleRegistration#isRunning()
     */
    @Override
    default boolean isRunning() {
        return getSource().isRunning();
    }
}
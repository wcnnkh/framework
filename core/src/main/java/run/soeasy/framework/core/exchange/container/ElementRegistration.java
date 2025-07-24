package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 元素注册接口，集成有效载荷注册与生命周期管理功能。
 * <p>
 * 该接口继承自{@link PayloadRegistration}和{@link LifecycleRegistration}，
 * 允许注册元素时携带有效载荷，并对注册对象进行生命周期控制（启动、停止）。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>载荷管理：支持获取和设置注册元素的载荷数据</li>
 *   <li>生命周期控制：提供注册对象的启动、停止状态管理</li>
 *   <li>注册组合：支持与其他注册对象的组合操作</li>
 * </ul>
 *
 * @param <V> 注册元素的载荷类型
 * 
 * @author shuchaowen
 * @see PayloadRegistration
 * @see LifecycleRegistration
 */
public interface ElementRegistration<V> extends PayloadRegistration<V>, LifecycleRegistration {

    /**
     * 设置注册元素的载荷数据并返回旧值
     * <p>
     * 该方法用于更新注册元素的载荷数据，调用后应触发相关事件通知（如元素变更事件）。
     * 
     * @param payload 新的载荷数据
     * @return 旧的载荷数据
     */
    V setPayload(V payload);

    /**
     * 组合当前注册与另一个注册
     * <p>
     * 若传入的注册为null或已取消，直接返回当前注册；
     * 否则返回组合后的注册对象（由{@link ElementRegistrationWrapped}实现）。
     * 
     * @param registration 要组合的注册对象
     * @return 组合后的元素注册对象
     */
    @Override
    default ElementRegistration<V> and(Registration registration) {
        if (registration == null || registration.isCancelled()) {
            return this;
        }
        return new ElementRegistrationWrapped<>(this, Elements.singleton(registration));
    }
}
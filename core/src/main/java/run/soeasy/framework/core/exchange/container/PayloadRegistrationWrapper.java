package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.RegistrationWrapper;

/**
 * 有效载荷注册包装器接口，用于包装{@link PayloadRegistration}实现类。
 * <p>
 * 该接口继承自{@link RegistrationWrapper}和{@link PayloadRegistration}，
 * 允许将一个{@link PayloadRegistration}实例包装为新的注册对象，
 * 并提供默认的方法转发实现。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>注册包装：通过{@link #getSource()}获取被包装的源注册对象</li>
 *   <li>载荷透传：默认实现将{@link #getPayload()}调用转发至源注册</li>
 *   <li>注册组合：默认实现将{@link #and(Registration)}调用转发至源注册</li>
 * </ul>
 *
 * @param <T> 有效载荷的类型
 * @param <W> 被包装的注册类型，需继承{@link PayloadRegistration}&lt;{@link T}&gt;
 * 
 * @author shuchaowen
 * @see PayloadRegistration
 * @see RegistrationWrapper
 */
@FunctionalInterface
public interface PayloadRegistrationWrapper<T, W extends PayloadRegistration<T>>
        extends RegistrationWrapper<W>, PayloadRegistration<T> {

    /**
     * 组合当前注册与另一个注册
     * <p>
     * 该方法默认将调用转发至被包装的源注册对象的{@link PayloadRegistration#and(Registration)}方法。
     * 
     * @param registration 要组合的注册对象
     * @return 组合后的注册对象
     * @see PayloadRegistration#and(Registration)
     */
    @Override
    default PayloadRegistration<T> and(Registration registration) {
        return getSource().and(registration);
    }

    /**
     * 获取有效载荷数据
     * <p>
     * 该方法默认将调用转发至被包装的源注册对象的{@link PayloadRegistration#getPayload()}方法。
     * 
     * @return 有效载荷数据
     * @see PayloadRegistration#getPayload()
     */
    @Override
    default T getPayload() {
        return getSource().getPayload();
    }
}
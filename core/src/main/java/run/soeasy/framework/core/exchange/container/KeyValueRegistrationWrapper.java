package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.RegistrationWrapper;

/**
 * 键值对注册包装器接口，用于透明包装{@link KeyValueRegistration}实例。
 * <p>
 * 该接口继承自{@link RegistrationWrapper}和{@link KeyValueRegistration}，
 * 提供对源注册对象的方法转发实现，确保包装器与源注册的行为一致性。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>透明转发：所有方法调用自动转发至被包装的源注册对象</li>
 *   <li>类型安全：通过泛型约束确保包装器与源注册的类型一致性</li>
 *   <li>键值对管理：继承键值对注册的所有特性</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <W> 被包装的源注册类型，需继承{@link KeyValueRegistration}&lt;{@link K}, {@link V}&gt;
 * 
 * @author soeasy.run
 * @see KeyValueRegistration
 * @see RegistrationWrapper
 */
public interface KeyValueRegistrationWrapper<K, V, W extends KeyValueRegistration<K, V>>
        extends RegistrationWrapper<W>, KeyValueRegistration<K, V> {

    /**
     * 组合当前注册与另一个注册
     * <p>
     * 该方法将调用转发至被包装的源注册对象的{@link KeyValueRegistration#and(Registration)}方法。
     * 
     * @param registration 要组合的注册对象
     * @return 组合后的键值对注册对象
     * @see KeyValueRegistration#and(Registration)
     */
    @Override
    default KeyValueRegistration<K, V> and(Registration registration) {
        return getSource().and(registration);
    }

    /**
     * 获取注册的键
     * <p>
     * 该方法将调用转发至被包装的源注册对象的{@link KeyValueRegistration#getKey()}方法。
     * 
     * @return 注册的键
     * @see KeyValueRegistration#getKey()
     */
    @Override
    default K getKey() {
        return getSource().getKey();
    }

    /**
     * 获取注册的值
     * <p>
     * 该方法将调用转发至被包装的源注册对象的{@link KeyValueRegistration#getValue()}方法。
     * 
     * @return 注册的值
     * @see KeyValueRegistration#getValue()
     */
    @Override
    default V getValue() {
        return getSource().getValue();
    }
}
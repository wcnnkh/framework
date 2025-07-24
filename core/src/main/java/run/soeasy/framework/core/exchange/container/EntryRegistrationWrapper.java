package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.exchange.Registration;

/**
 * 条目注册包装器接口，用于透明包装{@link EntryRegistration}实例。
 * <p>
 * 该接口继承自{@link EntryRegistration}和{@link KeyValueRegistrationWrapper}，
 * 提供对源注册对象的方法转发实现，确保包装器与源注册的行为一致性。
 * </p>
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>透明转发：所有方法调用自动转发至被包装的源注册对象</li>
 * <li>类型安全：通过泛型约束确保包装器与源注册的类型一致性</li>
 * <li>键值对操作：支持获取和修改键值对内容</li>
 * <li>注册组合：支持与其他注册对象的组合操作</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <W> 被包装的源注册类型，需继承{@link EntryRegistration}&lt;{@link K}, {@link V}&gt;
 * 
 * @author shuchaowen
 * @see EntryRegistration
 * @see KeyValueRegistrationWrapper
 */
public interface EntryRegistrationWrapper<K, V, W extends EntryRegistration<K, V>>
		extends EntryRegistration<K, V>, KeyValueRegistrationWrapper<K, V, W> {

	/**
	 * 获取注册条目的值
	 * <p>
	 * 该方法将调用转发至被包装的源注册对象的{@link EntryRegistration#getValue()}方法。
	 * 
	 * @return 注册条目的当前值
	 * @see EntryRegistration#getValue()
	 */
	@Override
	default V getValue() {
		return getSource().getValue();
	}

	/**
	 * 获取注册条目的键
	 * <p>
	 * 该方法将调用转发至被包装的源注册对象的{@link EntryRegistration#getKey()}方法。
	 * 
	 * @return 注册条目的键
	 * @see EntryRegistration#getKey()
	 */
	@Override
	default K getKey() {
		return getSource().getKey();
	}

	/**
	 * 设置注册条目的值，并返回旧值
	 * <p>
	 * 该方法将调用转发至被包装的源注册对象的{@link EntryRegistration#setValue(Object)}方法。
	 * 
	 * @param value 新的值
	 * @return 旧的值
	 * @see EntryRegistration#setValue(Object)
	 */
	@Override
	default V setValue(V value) {
		return getSource().setValue(value);
	}

	/**
	 * 组合当前注册与另一个注册
	 * <p>
	 * 该方法将调用转发至{@link EntryRegistration#and(Registration)}的默认实现，
	 * 最终转发至被包装的源注册对象的{@link EntryRegistration#and(Registration)}方法。
	 * 
	 * @param registration 要组合的注册对象
	 * @return 组合后的条目注册对象
	 * @see EntryRegistration#and(Registration)
	 */
	@Override
	default EntryRegistration<K, V> and(Registration registration) {
		return EntryRegistration.super.and(registration);
	}
}
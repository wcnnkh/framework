package io.basc.framework.util.register;

import io.basc.framework.util.element.ServiceLoader;

/**
 * 定义一个注册表
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Registry<T, R extends Registration> extends ServiceLoader<T> {
	/**
	 * 注册一个
	 * 
	 * @param element
	 * @return
	 * @throws RegistrationException
	 */
	R register(T element) throws RegistrationException;

	/**
	 * 批量注册
	 * 
	 * @param elements
	 * @return
	 * @throws RegistrationException
	 */
	default Registrations<R> registers(Iterable<? extends T> elements) throws RegistrationException {
		return Registration.registers(elements, this::register);
	}
}

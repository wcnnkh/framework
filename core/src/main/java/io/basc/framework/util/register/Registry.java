package io.basc.framework.util.register;

/**
 * 定义一个注册表
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Registry<T, R extends PayloadRegistration<T>> extends Registrations<R> {
	/**
	 * 注册一个
	 * 
	 * @param item
	 * @return
	 * @throws RegistrationException
	 */
	R register(T item) throws RegistrationException;

	/**
	 * 批量注册
	 * 
	 * @param items
	 * @return
	 * @throws RegistrationException
	 */
	default Registrations<R> registers(Iterable<? extends T> items) throws RegistrationException {
		return Registration.registers(items, this::register);
	}
}

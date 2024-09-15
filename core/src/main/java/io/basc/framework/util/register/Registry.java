package io.basc.framework.util.register;

import io.basc.framework.util.Listable;
import io.basc.framework.util.Registration;

/**
 * 定义一个注册表
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Registry<E> extends Listable<E> {
	/**
	 * 注册一个
	 * 
	 * @param element
	 * @return
	 * @throws RegistrationException
	 */
	Registration register(E element) throws RegistrationException;

	/**
	 * 批量注册
	 * 
	 * @param elements
	 * @return
	 * @throws RegistrationException
	 */
	default Registration registers(Iterable<? extends E> elements) throws RegistrationException {
		return Registration.registers(elements, this::register);
	}
}

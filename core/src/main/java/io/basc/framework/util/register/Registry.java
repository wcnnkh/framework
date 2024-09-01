package io.basc.framework.util.register;

import io.basc.framework.util.Elements;

/**
 * 定义一个注册表
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Registry<E> {
	/**
	 * 获取注册的元素
	 * 
	 * @return
	 */
	Elements<E> getElements();

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

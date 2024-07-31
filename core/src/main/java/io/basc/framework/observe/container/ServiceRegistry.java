package io.basc.framework.observe.container;

import io.basc.framework.observe.Observable;
import io.basc.framework.observe.register.RegistryEvent;
import io.basc.framework.util.element.ServiceLoader;
import io.basc.framework.util.register.BatchRegistration;
import io.basc.framework.util.register.PayloadRegistration;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.RegistrationException;

/**
 * 注册表
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface ServiceRegistry<E> extends ServiceLoader<E>, Observable<RegistryEvent<E>> {

	PayloadRegistration<E> register(E element) throws RegistrationException;

	BatchRegistration<PayloadRegistration<E>> getRegistrations();

	default BatchRegistration<PayloadRegistration<E>> registers(Iterable<? extends E> elements)
			throws RegistrationException {
		return Registration.registers(elements, this::register);
	}
}
package io.basc.framework.observe.container;

import io.basc.framework.observe.Observable;
import io.basc.framework.observe.register.RegistryEvent;
import io.basc.framework.register.PayloadRegistration;
import io.basc.framework.register.Registration;
import io.basc.framework.register.RegistrationException;
import io.basc.framework.register.Registrations;
import io.basc.framework.util.element.ServiceLoader;

/**
 * 注册表
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface ServiceRegistry<E> extends ServiceLoader<E>, Observable<RegistryEvent<E>> {

	PayloadRegistration<E> register(E element) throws RegistrationException;

	Registrations<PayloadRegistration<E>> getRegistrations();

	default Registrations<PayloadRegistration<E>> registers(Iterable<? extends E> elements)
			throws RegistrationException {
		return Registration.registers(elements, this::register);
	}
}
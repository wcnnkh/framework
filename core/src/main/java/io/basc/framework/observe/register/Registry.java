package io.basc.framework.observe.register;

import io.basc.framework.observe.Observable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;
import io.basc.framework.util.RegistrationException;
import io.basc.framework.util.Registrations;
import io.basc.framework.util.element.ServiceLoader;

/**
 * 注册表
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface Registry<E> extends ServiceLoader<E>, Observable<RegistryEvent<E>> {

	Registration register(E element) throws RegistrationException;

	Registrations<ElementRegistration<E>> clear() throws RegistrationException;

	default Registrations<ElementRegistration<E>> registers(Iterable<? extends E> elements)
			throws RegistrationException {
		Assert.requiredArgument(elements != null, "elements");
		return Registrations.register(elements.iterator(), (element) -> {
			Registration registration = register(element);
			if (registration.isEmpty()) {
				return ElementRegistration.empty();
			}
			return new ElementRegistration<E>(element, registration);
		});
	}
}
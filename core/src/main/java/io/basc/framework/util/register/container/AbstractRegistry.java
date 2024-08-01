package io.basc.framework.util.register.container;

import java.util.Arrays;
import java.util.function.Supplier;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.AbstractPayloadRegistration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.Registrations;
import io.basc.framework.util.register.Registry;

public abstract class AbstractRegistry<E, C, R extends AbstractPayloadRegistration<E>, RS extends Registrations<R>>
		extends LazyContainer<C> implements Registry<E, R> {

	public AbstractRegistry(Supplier<? extends C> containerSupplier) {
		super(containerSupplier);
	}

	/**
	 * 创建一个Registration
	 * 
	 * @param item
	 * @return
	 */
	protected abstract R createRegistration(E item);

	/**
	 * 组合多个Registration
	 * 
	 * @param registrations
	 * @return
	 */
	protected abstract RS createRegistrations(Elements<R> registrations);

	/**
	 * 在注册后执行
	 * 
	 * @param registrations
	 * @return
	 */
	protected abstract RS postRegisterAfter(RS registrations);

	/**
	 * 会有线程安全的环境中执行
	 * 
	 * @param container
	 * @param registration
	 * @return
	 */
	protected abstract boolean register(C container, R registration);

	@Override
	public final R register(E registration) throws RegistrationException {
		// 强制使用批量注册
		Registrations<R> registrations = registers(Arrays.asList(registration));
		return registrations.getRegistrations().first();
	}

	@Override
	public final RS registers(Iterable<? extends E> items) throws RegistrationException {
		Elements<R> registrations = Elements.of(items).map((this::createRegistration));
		RS rs = write((container) -> {
			registrations.forEach((registration) -> {
				if (!register(container, registration)) {
					registration.getLimiter().limited();
				}
			});
			return createRegistrations(registrations.filter((e) -> !e.isInvalid()));
		});
		return postRegisterAfter(rs);
	}
}

package io.basc.framework.register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.function.ConsumeProcessor;
import io.basc.framework.util.function.Processor;

public interface Registration {

	static final Registration EMPTY = new Registration() {

		@Override
		public Registration and(Registration registration) {
			if (registration == null) {
				return this;
			}

			return registration;
		}

		@Override
		public boolean isInvalid() {
			return true;
		}

		@Override
		public void unregister() {
		}
	};

	public static <E extends Registration, S, X extends Throwable> Registrations<E> registers(
			Iterable<? extends S> iterable, Processor<? super S, ? extends E, ? extends X> register) throws X {
		Assert.requiredArgument(iterable != null, "iterable");
		return registers(iterable.iterator(), register);
	}

	public static <E extends Registration, S, X extends Throwable> Registrations<E> registers(
			Iterator<? extends S> iterator, Processor<? super S, ? extends E, ? extends X> register) throws X {
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.requiredArgument(register != null, "registry");
		List<E> registrations = null;
		while (iterator.hasNext()) {
			S service = iterator.next();
			if (service == null) {
				continue;
			}

			E registration;
			try {
				registration = register.process(service);
			} catch (Throwable e) {
				if (registrations != null) {
					try {
						Collections.reverse(registrations);
						ConsumeProcessor.consumeAll(registrations, (reg) -> reg.unregister());
					} catch (Throwable e2) {
						e.addSuppressed(e2);
					}
				}
				throw e;
			}

			if (registration.isInvalid()) {
				continue;
			}

			if (registrations == null) {
				registrations = new ArrayList<>(8);
			}
			registrations.add(registration);
		}
		return new Registrations<>(Elements.of(registrations));
	}

	default Registration and(Registration registration) {
		if (registration == null || registration == Registration.EMPTY) {
			return this;
		}
		return new JoinRegistration(this, registration);
	}

	/**
	 * 是否是无效的
	 * 
	 * @return
	 */
	default boolean isInvalid() {
		return this == EMPTY;
	}

	void unregister() throws RegistrationException;
}

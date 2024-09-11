package io.basc.framework.util.observe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.function.ConsumeProcessor;
import io.basc.framework.util.function.Processor;

public interface Registration {
	static final Registration CANCELLED = new Cancelled();

	default Registration and(Registration registration) {
		if (registration == null || registration.isCancelled()) {
			return this;
		}

		Elements<Registration> elements = Elements.forArray(this, registration);
		return Registrations.of(elements);
	}

	/**
	 * 取消
	 * 
	 * @return
	 */
	boolean cancel();

	/**
	 * 是否可以取消 returns {@code true} if and only if the operation can be cancelled via
	 * {@link #cancel()}.
	 */
	boolean isCancellable();

	/**
	 * 是否已取消
	 * 
	 * @return
	 */
	boolean isCancelled();

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
						ConsumeProcessor.consumeAll(registrations, (reg) -> reg.cancel());
					} catch (Throwable e2) {
						e.addSuppressed(e2);
					}
				}
				throw e;
			}

			if (registration.isCancelled()) {
				continue;
			}

			if (registrations == null) {
				registrations = new ArrayList<>(8);
			}
			registrations.add(registration);
		}

		Elements<E> elements = Elements.of(registrations);
		return () -> elements;
	}
}

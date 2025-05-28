package run.soeasy.framework.core.exchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.core.function.ThrowingFunction;

public interface Registration {
	static final Registration FAILURE = new Registed(true);
	static final Registration SUCCESS = new Registed(false);

	default Registration and(Registration registration) {
		if (registration == null || registration.isCancelled()) {
			return this;
		}

		Elements<Registration> elements = Elements.forArray(this, registration);
		return Registrations.forElements(elements);
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
			@NonNull Iterable<? extends S> iterable, ThrowingFunction<? super S, ? extends E, ? extends X> register)
			throws X {
		return registers(iterable.iterator(), register);
	}

	public static <E extends Registration, S, X extends Throwable> Registrations<E> registers(
			@NonNull Iterator<? extends S> iterator,
			@NonNull ThrowingFunction<? super S, ? extends E, ? extends X> register) throws X {
		List<E> registrations = null;
		while (iterator.hasNext()) {
			S service = iterator.next();
			if (service == null) {
				continue;
			}

			E registration;
			try {
				registration = register.apply(service);
			} catch (Throwable e) {
				if (registrations != null) {
					try {
						Collections.reverse(registrations);
						ThrowingConsumer.acceptAll(registrations.iterator(), (reg) -> reg.cancel());
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

package run.soeasy.framework.util.exchange;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.lang.Wrapper;
import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.collection.Elements;
import run.soeasy.framework.util.function.Consumer;
import run.soeasy.framework.util.function.Function;

public interface Registration {

	public static interface RegistrationWrapper<W extends Registration> extends Registration, Wrapper<W> {
		@Override
		default boolean isCancellable() {
			return getSource().isCancellable();
		}

		@Override
		default boolean cancel() {
			return getSource().cancel();
		}

		@Override
		default boolean isCancelled() {
			return getSource().isCancelled();
		}
	}

	@RequiredArgsConstructor
	public static class Registed implements Registration, Serializable {
		private static final long serialVersionUID = 1L;
		private final boolean cancelled;

		@Override
		public boolean cancel() {
			return false;
		}

		@Override
		public boolean isCancellable() {
			return false;
		}

		@Override
		public boolean isCancelled() {
			return cancelled;
		}
	}

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
			Iterable<? extends S> iterable, Function<? super S, ? extends E, ? extends X> register) throws X {
		Assert.requiredArgument(iterable != null, "iterable");
		return registers(iterable.iterator(), register);
	}

	public static <E extends Registration, S, X extends Throwable> Registrations<E> registers(
			Iterator<? extends S> iterator, Function<? super S, ? extends E, ? extends X> register) throws X {
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
				registration = register.apply(service);
			} catch (Throwable e) {
				if (registrations != null) {
					try {
						Collections.reverse(registrations);
						Consumer.acceptAll(registrations.iterator(), (reg) -> reg.cancel());
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

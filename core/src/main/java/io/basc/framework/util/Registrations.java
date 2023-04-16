package io.basc.framework.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.stream.Stream;

public class Registrations<T extends Registration> implements Registration {
	private static class AndRegistration<R extends Registration> extends Registrations<R> {
		private final Registration and;
		private final Registrations<R> registrations;

		public AndRegistration(Registrations<R> registrations, Registration and) {
			super(registrations.elements);
			this.registrations = registrations;
			this.and = and;
		}

		@Override
		public boolean isEmpty() {
			return registrations.isEmpty() && and.isEmpty();
		}

		@Override
		public void unregister() throws RegistrationException {
			try {
				and.unregister();
			} finally {
				super.unregister();
			}
		}
	}

	private static class AndRegistrations<R extends Registration> extends Registrations<R> {
		private final Registrations<R> left;
		private final Registrations<R> right;

		public AndRegistrations(Registrations<R> left, Registrations<R> right) {
			super(Elements.concat(left.elements, right.elements));
			this.left = left;
			this.right = right;
		}

		@Override
		public boolean isEmpty() {
			return left.isEmpty() && right.isEmpty();
		}

		@Override
		public void unregister() throws RegistrationException {
			try {
				right.unregister();
			} finally {
				left.unregister();
			}
		}
	}

	private static class VersionRegistrations<R extends Registration> extends Registrations<R> {
		private final Registrations<R> registrations;
		private final long version;
		private final LongSupplier versionSupplier;

		public VersionRegistrations(Registrations<R> registrations, LongSupplier versionSupplier) {
			super(registrations.elements);
			this.registrations = registrations;
			this.version = versionSupplier.getAsLong();
			this.versionSupplier = versionSupplier;
		}

		@Override
		public boolean isEmpty() {
			return version != versionSupplier.getAsLong() || registrations.isEmpty();
		}

		@Override
		public void unregister() throws RegistrationException {
			registrations.unregister();
		}
	}

	private static final Registrations<?> EMPTY = new Registrations<>(Elements.empty());

	@SuppressWarnings("unchecked")
	public static <R extends Registration> Registrations<R> empty() {
		return (Registrations<R>) EMPTY;
	}

	public static <E extends Registration, S, X extends Throwable> Registrations<E> register(
			Iterator<? extends S> iterator, Processor<? super S, ? extends E, ? extends X> registry) throws X {
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.requiredArgument(registry != null, "registry");
		List<E> registrations = null;
		while (iterator.hasNext()) {
			S service = iterator.next();
			if (service == null) {
				continue;
			}

			E registration;
			try {
				registration = registry.process(service);
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

			if (registration.isEmpty()) {
				continue;
			}

			if (registrations == null) {
				registrations = new ArrayList<>(8);
			}
			registrations.add(registration);
		}

		if (CollectionUtils.isEmpty(registrations)) {
			return empty();
		}
		return new Registrations<>(Elements.of(registrations));
	}

	private final Elements<T> elements;

	public Registrations(Elements<T> elements) {
		Assert.requiredArgument(elements != null, "elements");
		this.elements = elements;
	}

	@Override
	public Registrations<T> and(Registration registration) {
		if (registration == null || registration.isEmpty()) {
			return this;
		}
		return new AndRegistration<>(this, registration);
	}

	public Registrations<T> and(Registrations<T> registrations) {
		return new AndRegistrations<>(this, registrations);
	}

	public Elements<T> getElements() {
		return elements;
	}

	@Override
	public boolean isEmpty() {
		Stream<T> stream = elements.stream();
		try {
			return stream.allMatch((e) -> e.isEmpty());
		} finally {
			stream.close();
		}
	}

	@Override
	public void unregister() throws RegistrationException {
		if (isEmpty()) {
			return;
		}
		ConsumeProcessor.consumeAll(elements.reverse(), (e) -> e.unregister());
	}

	@Override
	public Registrations<T> version(LongSupplier versionSuppler) {
		return new VersionRegistrations<>(this, versionSuppler);
	}
}

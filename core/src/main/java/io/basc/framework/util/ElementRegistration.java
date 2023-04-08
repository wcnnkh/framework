package io.basc.framework.util;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ElementRegistration<E> implements Registration {
	private final Elements<E> elements;
	private final AtomicBoolean unregister = new AtomicBoolean();

	public ElementRegistration(Elements<E> elements) {
		this.elements = elements;
	}

	public Elements<E> getElements() {
		return elements;
	}

	@Override
	public final void unregister() throws RegistrationException {
		if (!isEmpty() && unregister.compareAndSet(false, true)) {
			unregister(elements);
		}
	}

	@Override
	public ElementRegistration<E> and(Registration registration) {
		if (registration == null || registration.isEmpty()) {
			return this;
		}

		return new AndRegistration<>(this, registration);
	}

	/**
	 * 只会执行一次
	 * 
	 * @param elements
	 */
	protected abstract void unregister(Elements<E> elements);

	@Override
	public boolean isEmpty() {
		return unregister.get();
	}

	private static class AndRegistration<T> extends ElementRegistration<T> {
		private final Registration and;
		private final ElementRegistration<T> registration;

		public AndRegistration(ElementRegistration<T> registration, Registration and) {
			super(registration.elements);
			this.registration = registration;
			this.and = and;
		}

		@Override
		protected void unregister(Elements<T> elements) {
			try {
				registration.unregister(elements);
			} finally {
				and.unregister();
			}
		}

		@Override
		public boolean isEmpty() {
			return registration.isEmpty() || super.isEmpty();
		}
	}
}

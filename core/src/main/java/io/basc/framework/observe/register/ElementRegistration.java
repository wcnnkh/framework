package io.basc.framework.observe.register;

import java.util.Objects;
import java.util.function.LongSupplier;

import io.basc.framework.util.DisposableRegistration;
import io.basc.framework.util.Registration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElementRegistration<E> extends DisposableRegistration {
	private static ElementRegistration<?> EMPTY = new ElementRegistration<>(null, Registration.EMPTY);

	@SuppressWarnings("unchecked")
	public static <R> ElementRegistration<R> empty() {
		return (ElementRegistration<R>) EMPTY;
	}

	private final E element;
	private Object equalsAndHashCode;

	public ElementRegistration(E element, Registration registration) {
		super(registration);
		this.element = element;
		this.equalsAndHashCode = element;
	}

	public E getElement() {
		return element;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof ElementRegistration) {
			ElementRegistration<?> elementRegistration = (ElementRegistration<?>) obj;
			return Objects.equals(equalsAndHashCode, elementRegistration.equalsAndHashCode);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(equalsAndHashCode);
	}

	@Override
	public ElementRegistration<E> and(Registration registration) {
		if (registration == null || registration.isInvalid()) {
			return this;
		}
		return new AndElementRegistration<>(this, registration);
	}

	@Override
	public ElementRegistration<E> version(LongSupplier versionSuppler) {
		return new VersionRegistration<>(this, versionSuppler);
	}

	private static class VersionRegistration<R> extends ElementRegistration<R> {
		private final LongSupplier versionSupplier;
		private final long version;

		public VersionRegistration(ElementRegistration<R> elementRegistration, LongSupplier versionSupplier) {
			super(elementRegistration.element, elementRegistration);
			this.version = versionSupplier.getAsLong();
			this.versionSupplier = versionSupplier;
		}

		@Override
		public boolean isInvalid() {
			return version != versionSupplier.getAsLong() || super.isInvalid();
		}
	}

	private static class AndElementRegistration<R> extends ElementRegistration<R> {

		public AndElementRegistration(ElementRegistration<R> elementRegistration, Registration registration) {
			super(elementRegistration.element, () -> {
				try {
					registration.unregister();
				} finally {
					elementRegistration.unregister();
				}
			});
		}
	}
}

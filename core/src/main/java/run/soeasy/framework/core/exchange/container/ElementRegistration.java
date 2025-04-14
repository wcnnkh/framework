package run.soeasy.framework.core.exchange.container;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

public interface ElementRegistration<V> extends PayloadRegistration<V>, LifecycleRegistration {

	public static class StandardElementRegistrationWrappe<V, W extends ElementRegistration<V>>
			extends StandardPayloadRegistrationWrapper<V, W> implements ElementRegistrationWrapper<V, W> {

		public StandardElementRegistrationWrappe(@NonNull W source,
				@NonNull Elements<Registration> relatedRegistrations) {
			super(source, relatedRegistrations);
		}

		protected StandardElementRegistrationWrappe(StandardPayloadRegistrationWrapper<V, W> context) {
			super(context);
		}

		@Override
		public StandardElementRegistrationWrappe<V, W> and(@NonNull Registration registration) {
			return new StandardElementRegistrationWrappe<>(super.and(registration));
		}
	}

	public static interface ElementRegistrationWrapper<E, W extends ElementRegistration<E>>
			extends ElementRegistration<E>, PayloadRegistrationWrapper<E, W>, LifecycleRegistrationWrapper<W> {

		@Override
		default E getPayload() {
			return getSource().getPayload();
		}

		@Override
		default ElementRegistration<E> and(Registration registration) {
			return ElementRegistration.super.and(registration);
		}

		@Override
		default E setPayload(E payload) {
			return getSource().setPayload(payload);
		}

		@Override
		default void start() {
			getSource().start();
		}

		@Override
		default void stop() {
			getSource().stop();
		}

		@Override
		default boolean isRunning() {
			return getSource().isRunning();
		}
	}

	V setPayload(V payload);

	@Override
	default ElementRegistration<V> and(Registration registration) {
		if (registration == null || registration.isCancelled()) {
			return this;
		}

		return new StandardElementRegistrationWrappe<>(this, Elements.singleton(registration));
	}
}

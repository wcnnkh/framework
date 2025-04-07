package run.soeasy.framework.util.register;

import java.util.function.Function;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.util.collection.Elements;
import run.soeasy.framework.util.exchange.Registration;

public interface PayloadRegistration<T> extends Registration {

	public static class StandardPayloadRegistrationWrapper<S, W extends PayloadRegistration<S>>
			extends StandardRegistrationWrapper<W> implements PayloadRegistrationWrapper<S, W> {

		public StandardPayloadRegistrationWrapper(@NonNull W source,
				@NonNull Elements<Registration> relatedRegistrations) {
			super(source, relatedRegistrations);
		}

		protected StandardPayloadRegistrationWrapper(StandardRegistrationWrapper<W> context) {
			super(context);
		}

		@Override
		public StandardPayloadRegistrationWrapper<S, W> and(@NonNull Registration registration) {
			return combine(registration);
		}

		@Override
		public StandardPayloadRegistrationWrapper<S, W> combine(@NonNull Registration registration) {
			return new StandardPayloadRegistrationWrapper<>(super.combine(registration));
		}
	}

	public static interface PayloadRegistrationWrapper<T, W extends PayloadRegistration<T>>
			extends RegistrationWrapper<W>, PayloadRegistration<T> {

		@Override
		default PayloadRegistration<T> and(Registration registration) {
			return getSource().and(registration);
		}

		@Override
		default T getPayload() {
			return getSource().getPayload();
		}
	}

	@Data
	public static class MappedPayloadRegistration<S, T, W extends PayloadRegistration<S>>
			implements PayloadRegistration<T>, RegistrationWrapper<W> {
		@NonNull
		private final W source;
		@NonNull
		private final Function<? super S, ? extends T> mapper;

		@Override
		public T getPayload() {
			S payload = source.getPayload();
			return mapper.apply(payload);
		}
	}

	public static class PayloadRegisted<T> extends Registed implements PayloadRegistration<T> {
		private static final long serialVersionUID = 1L;
		private final T payload;

		public PayloadRegisted(boolean cancelled, T payload) {
			super(cancelled);
			this.payload = payload;
		}

		@Override
		public PayloadRegistration<T> and(Registration registration) {
			return PayloadRegistration.super.and(registration);
		}

		@Override
		public T getPayload() {
			return payload;
		}
	}

	static final PayloadRegistration<?> FAILURE = new PayloadRegisted<>(true, null);
	static final PayloadRegistration<?> SUCCESS = new PayloadRegisted<>(false, null);

	@SuppressWarnings("unchecked")
	public static <E> PayloadRegisted<E> failure() {
		return (PayloadRegisted<E>) FAILURE;
	}

	public static <E> PayloadRegisted<E> failure(E payload) {
		return new PayloadRegisted<E>(true, payload);
	}

	@SuppressWarnings("unchecked")
	public static <E> PayloadRegisted<E> success() {
		return (PayloadRegisted<E>) SUCCESS;
	}

	public static <E> PayloadRegisted<E> success(E payload) {
		return new PayloadRegisted<E>(false, payload);
	}

	T getPayload();

	default <R> PayloadRegistration<R> map(@NonNull Function<? super T, ? extends R> mapper) {
		return new MappedPayloadRegistration<>(this, mapper);
	}

	@Override
	default PayloadRegistration<T> and(Registration registration) {
		if (registration == null || registration.isCancelled()) {
			return this;
		}
		return new StandardPayloadRegistrationWrapper<>(this, Elements.singleton(registration));
	}
}
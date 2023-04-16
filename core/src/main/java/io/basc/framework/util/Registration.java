package io.basc.framework.util;

import java.util.function.LongSupplier;

public interface Registration {

	static final Registration EMPTY = new Registration() {

		@Override
		public void unregister() {
		}

		@Override
		public Registration disposable() {
			return this;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public Registration and(Registration registration) {
			if (registration == null) {
				return this;
			}

			if (ClassUtils.isLambdaClass(registration.getClass())) {
				return DisposableRegistration.of(registration);
			}
			return registration;
		}

		@Override
		public Registration version(LongSupplier versionSuppler) {
			return this;
		}
	};

	void unregister() throws RegistrationException;

	default boolean isEmpty() {
		return this == EMPTY;
	}

	default Registration disposable() {
		return DisposableRegistration.of(this);
	}

	default Registration version(LongSupplier versionSuppler) {
		return new VersionRegistration(versionSuppler, this);
	}

	default Registration and(Registration registration) {
		return MergedRegistration.merge(this, registration);
	}
}

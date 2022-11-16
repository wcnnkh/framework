package io.basc.framework.util;

public interface Registration {

	static final Registration EMPTY = new Registration() {

		@Override
		public void unregister() {
		}

		@Override
		public Registration disposable() {
			return this;
		}
	};

	void unregister();

	default Registration disposable() {
		return new DisposableRegistration(this);
	}

	default Registration and(Registration registration) {
		if (registration == null || registration == EMPTY) {
			return this;
		}

		if (this == EMPTY) {
			return registration;
		}

		return () -> {
			try {
				unregister();
			} finally {
				registration.unregister();
			}
		};
	}
}

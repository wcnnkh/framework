package io.basc.framework.util;

public interface Registration {

	static final Registration EMPTY = () -> {
	};

	void unregister();

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

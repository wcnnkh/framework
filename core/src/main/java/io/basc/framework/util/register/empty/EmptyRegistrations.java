package io.basc.framework.util.register.empty;

import io.basc.framework.util.Elements;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.Registrations;

public class EmptyRegistrations<R extends Registration> extends EmptyRegistration implements Registrations<R> {
	private static final EmptyRegistrations<?> EMPTY = new EmptyRegistrations<>();

	@SuppressWarnings("unchecked")
	public static <E extends Registration> Registrations<E> empty() {
		return (Registrations<E>) EMPTY;
	}

	@Override
	public Elements<R> getRegistrations() {
		return Elements.empty();
	}
}

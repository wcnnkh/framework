package io.basc.framework.util;

public class EmptyElementRegistry<E> implements ElementRegistry<E> {
	public static final EmptyElementRegistry<?> EMPTY = new EmptyElementRegistry<>();

	@Override
	public Elements<E> getElements() {
		return Elements.empty();
	}

	@Override
	public ElementRegistration<E> register(E element) throws RegistrationException {
		return ElementRegistration.empty();
	}

	@Override
	public Registrations<ElementRegistration<E>> clear() throws RegistrationException {
		return Registrations.empty();
	}

}

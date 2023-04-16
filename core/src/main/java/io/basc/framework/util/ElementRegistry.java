package io.basc.framework.util;

public interface ElementRegistry<E> {

	/**
	 * @param <R>
	 * @return 始终返回同一对象
	 */
	@SuppressWarnings("unchecked")
	public static <R> ElementRegistry<R> empty() {
		return (ElementRegistry<R>) EmptyElementRegistry.EMPTY;
	}

	Elements<E> getElements();

	Registration register(E element) throws RegistrationException;

	Registrations<ElementRegistration<E>> clear() throws RegistrationException;

	default Registrations<ElementRegistration<E>> registers(Iterable<? extends E> elements)
			throws RegistrationException {
		Assert.requiredArgument(elements != null, "elements");
		return Registrations.register(elements.iterator(), (element) -> {
			Registration registration = register(element);
			if (registration.isEmpty()) {
				return ElementRegistration.empty();
			}
			return new ElementRegistration<E>(element, registration);
		});
	}
}
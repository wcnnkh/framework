package io.basc.framework.util.register.container;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.CombinableRegistration;
import io.basc.framework.util.register.Registration;
import lombok.NonNull;

public class ElementRegistration<T> extends ContainerRegistration<T> {
	private final T payload;

	public ElementRegistration(T payload) {
		this.payload = payload;
	}

	protected ElementRegistration(@NonNull ElementRegistration<T> containerRegistration) {
		this(containerRegistration, containerRegistration.payload);
	}

	private ElementRegistration(CombinableRegistration<Registration> context, T payload) {
		super(context);
		this.payload = payload;
	}

	@Override
	public T getPayload() {
		return payload;
	}

	@Override
	public ElementRegistration<T> and(@NonNull Registration registration) {
		return new ElementRegistration<>(super.and(registration), this.payload);
	}

	@Override
	public ElementRegistration<T> andAll(@NonNull Elements<? extends Registration> registrations) {
		return new ElementRegistration<>(super.andAll(registrations), this.payload);
	}
}

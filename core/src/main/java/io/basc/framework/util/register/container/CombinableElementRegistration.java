package io.basc.framework.util.register.container;

import io.basc.framework.util.Elements;
import io.basc.framework.util.register.CombinablePayloadRegistration;
import io.basc.framework.util.register.Registration;
import lombok.NonNull;

public class CombinableElementRegistration<V, W extends ElementRegistration<V>>
		extends CombinablePayloadRegistration<V, W> implements ElementRegistration<V> {

	public CombinableElementRegistration(W source, Registration registration) {
		super(source, registration);
	}

	protected CombinableElementRegistration(CombinablePayloadRegistration<V, W> combinableServiceRegistration) {
		super(combinableServiceRegistration);
	}

	@Override
	public V getValue() {
		return source.getValue();
	}

	@Override
	public V setValue(V value) {
		return source.setValue(value);
	}

	@Override
	public CombinableElementRegistration<V, W> and(@NonNull Registration registration) {
		return combine(registration);
	}

	@Override
	public CombinableElementRegistration<V, W> combine(@NonNull Registration registration) {
		return new CombinableElementRegistration<>(super.combine(registration));
	}

	@Override
	public CombinableElementRegistration<V, W> combineAll(@NonNull Elements<? extends Registration> registrations) {
		return new CombinableElementRegistration<>(super.combineAll(registrations));
	}
}

package io.basc.framework.util.register;

import java.util.function.BooleanSupplier;

import io.basc.framework.util.Elements;

public class CombinableKeyValueRegistration<K, V, W extends KeyValueRegistration<K, V>>
		extends CombinableRegistration<Registration> implements KeyValueRegistrationWrapper<K, V, W> {
	private final W source;

	public CombinableKeyValueRegistration(W source, Registration registration) {
		super(Elements.singleton(registration));
		this.source = source;
	}

	protected CombinableKeyValueRegistration(CombinableKeyValueRegistration<K, V, W> combinableKeyValueRegistration) {
		this(combinableKeyValueRegistration.source, combinableKeyValueRegistration);
	}

	private CombinableKeyValueRegistration(W source, CombinableRegistration<Registration> context) {
		super(context);
		this.source = source;
	}

	@Override
	public CombinableKeyValueRegistration<K, V, W> and(Registration registration) {
		return new CombinableKeyValueRegistration<>(source, super.combine(registration));
	}

	@Override
	public boolean isInvalid(BooleanSupplier checker) {
		return super.isInvalid(() -> source.isInvalid() && checker.getAsBoolean());
	}

	@Override
	public void deregister(Runnable runnable) throws RegistrationException {
		super.deregister(() -> {
			try {
				source.deregister();
			} finally {
				runnable.run();
			}
		});
	}

	@Override
	public W getSource() {
		return source;
	}
}

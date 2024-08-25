package io.basc.framework.util.register;

import java.util.function.BooleanSupplier;

import io.basc.framework.util.element.Elements;

public class CombinableKeyValueRegistration<K, V> extends CombinableRegistration<Registration>
		implements KeyValueRegistration<K, V> {
	private final KeyValueRegistration<K, V> keyValueRegistration;

	public CombinableKeyValueRegistration(KeyValueRegistration<K, V> keyValueRegistration, Registration registration) {
		super(Elements.singleton(registration));
		this.keyValueRegistration = keyValueRegistration;
	}

	protected CombinableKeyValueRegistration(CombinableKeyValueRegistration<K, V> combinableKeyValueRegistration) {
		this(combinableKeyValueRegistration.keyValueRegistration, combinableKeyValueRegistration);
	}

	private CombinableKeyValueRegistration(KeyValueRegistration<K, V> keyValueRegistration,
			CombinableRegistration<Registration> context) {
		super(context);
		this.keyValueRegistration = keyValueRegistration;
	}

	@Override
	public K getKey() {
		return keyValueRegistration.getKey();
	}

	@Override
	public V getValue() {
		return keyValueRegistration.getValue();
	}

	@Override
	public CombinableKeyValueRegistration<K, V> and(Registration registration) {
		return new CombinableKeyValueRegistration<>(keyValueRegistration, super.combine(registration));
	}

	@Override
	public boolean isInvalid(BooleanSupplier checker) {
		return super.isInvalid(() -> keyValueRegistration.isInvalid() && checker.getAsBoolean());
	}

	@Override
	public void deregister(Runnable runnable) throws RegistrationException {
		super.deregister(() -> {
			try {
				keyValueRegistration.deregister();
			} finally {
				runnable.run();
			}
		});
	}
}

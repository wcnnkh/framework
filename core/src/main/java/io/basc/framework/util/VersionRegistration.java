package io.basc.framework.util;

import java.util.function.LongSupplier;

import lombok.Getter;

@Getter
public class VersionRegistration extends DisposableRegistration {
	private final long version;
	private final LongSupplier versionSupplier;

	public VersionRegistration(LongSupplier versionSupplier, Registration registration) {
		super(registration);
		this.version = versionSupplier.getAsLong();
		this.versionSupplier = versionSupplier;
	}

	@Override
	public boolean isEmpty() {
		return version != versionSupplier.getAsLong() || super.isEmpty();
	}
}

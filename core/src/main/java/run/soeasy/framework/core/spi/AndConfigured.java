package run.soeasy.framework.core.spi;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.Registration;

public class AndConfigured<S, W extends Configured<S>> extends AndInclude<S, W> implements ConfiguredWrapper<S, W> {

	public AndConfigured(@NonNull W source, @NonNull Registration registration) {
		super(source, registration);
	}

	@Override
	public Configured<S> and(Registration registration) {
		return ConfiguredWrapper.super.and(registration);
	}
}
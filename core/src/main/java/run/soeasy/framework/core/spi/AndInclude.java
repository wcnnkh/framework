package run.soeasy.framework.core.spi;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.exchange.Registration;

@Getter
@RequiredArgsConstructor
public class AndInclude<S, W extends Include<S>> implements IncludeWrapper<S, W> {
	@NonNull
	private final W source;
	@NonNull
	private final Registration registration;

	@Override
	public Include<S> and(Registration registration) {
		return new AndInclude<>(source, this.registration.and(registration));
	}

	@Override
	public boolean cancel() {
		return IncludeWrapper.super.cancel() && registration.cancel();
	}

	@Override
	public boolean isCancellable() {
		return IncludeWrapper.super.isCancellable() || registration.isCancellable();
	}

	@Override
	public boolean isCancelled() {
		return IncludeWrapper.super.isCancelled() && registration.isCancelled();
	}
}
package io.basc.framework.util.observe;

import java.util.function.BooleanSupplier;

import io.basc.framework.util.concurrent.limit.DisposableLimiter;
import lombok.NonNull;

public class DisposableRegistration extends LimitableRegistration {
	@NonNull
	private final Runnable runnable;

	public DisposableRegistration(@NonNull Runnable runnable) {
		super(new DisposableLimiter());
		this.runnable = runnable;
	}

	@Override
	public boolean cancel(BooleanSupplier cancel) {
		return super.cancel(() -> {
			if (cancel.getAsBoolean()) {
				runnable.run();
				return true;
			}
			return false;
		});
	}
}

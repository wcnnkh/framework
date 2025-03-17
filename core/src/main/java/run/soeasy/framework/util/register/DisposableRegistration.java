package run.soeasy.framework.util.register;

import java.util.function.BooleanSupplier;

import lombok.NonNull;

public class DisposableRegistration extends AbstractLifecycleRegistration {
	@NonNull
	private final Runnable runnable;

	public DisposableRegistration(@NonNull Runnable runnable) {
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

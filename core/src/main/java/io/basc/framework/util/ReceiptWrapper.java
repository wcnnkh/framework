package io.basc.framework.util;

public interface ReceiptWrapper<W extends Receipt> extends Receipt, RegistrationWrapper<W> {
	@Override
	default Throwable cause() {
		return getSource().cause();
	}

	@Override
	default boolean isDone() {
		return getSource().isDone();
	}

	@Override
	default boolean isSuccess() {
		return getSource().isSuccess();
	}
}

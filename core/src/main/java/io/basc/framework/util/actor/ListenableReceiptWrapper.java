package io.basc.framework.util.actor;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.Receipt;
import io.basc.framework.util.ReceiptWrapper;

public interface ListenableReceiptWrapper<T extends Receipt, W extends ListenableReceipt<T>>
		extends ListenableReceipt<T>, ListenableRegistrationWrapper<T, W>, ReceiptWrapper<W> {

	@Override
	default boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return getSource().await(timeout, unit);
	}

	@Override
	default ListenableReceipt<T> sync() {
		return getSource().sync();
	}

	@Override
	default ListenableReceipt<T> syncInterruptibly() throws InterruptedException {
		return getSource().syncInterruptibly();
	}
}

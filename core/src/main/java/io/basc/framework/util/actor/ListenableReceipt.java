package io.basc.framework.util.actor;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.Receipt;

public interface ListenableReceipt<T extends Receipt> extends ListenableRegistration<T>, Receipt {

	public static interface ListenableReceiptWrapper<T extends Receipt, W extends ListenableReceipt<T>>
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

	/**
	 * 等待
	 * 
	 * @param timeout
	 * @param unit
	 * @return 是否已完成
	 * @throws InterruptedException
	 */
	boolean await(long timeout, TimeUnit unit) throws InterruptedException;

	@Override
	default ListenableReceipt<T> sync() {
		while (true) {
			try {
				if (await(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
					break;
				}
			} catch (InterruptedException e) {
				// 忽略此异常一直等
			}
		}
		return this;
	}

	default ListenableReceipt<T> syncInterruptibly() throws InterruptedException {
		while (await(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
			break;
		}
		return this;
	}
}

package io.basc.framework.util.future;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.Listenable;
import io.basc.framework.util.Receipt;

public interface ListenableReceipt<T extends Receipt> extends Listenable<T>, Receipt {

	/**
	 * 等待
	 * 
	 * @param timeout
	 * @param unit
	 * @return 是否已完成
	 * @throws InterruptedException
	 */
	boolean await(long timeout, TimeUnit unit) throws InterruptedException;

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

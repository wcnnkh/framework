package scw.core;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 多任务同步
 * 
 * @author shuchaowen
 */
public final class Multitask extends CopyOnWriteArrayList<Runnable> {
	private static final long serialVersionUID = 1L;

	public CountDownLatch execute() {
		final CountDownLatch countDownLatch = new CountDownLatch(size());
		for (final Runnable runnable : this) {
			new Thread(new Runnable() {

				public void run() {
					try {
						runnable.run();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						countDownLatch.countDown();
					}
				}
			}).start();
		}
		return countDownLatch;
	}

	public void executeAndAwait() throws InterruptedException {
		CountDownLatch countDownLatch = execute();
		if (countDownLatch == null) {
			return;
		}

		countDownLatch.await();
	}

	public void executeAndAwait(long timeout, TimeUnit unit) throws InterruptedException {
		CountDownLatch countDownLatch = execute();
		if (countDownLatch == null) {
			return;
		}

		countDownLatch.await(timeout, unit);
	}
}
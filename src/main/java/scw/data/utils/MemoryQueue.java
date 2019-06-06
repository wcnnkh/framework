package scw.data.utils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import scw.core.BlockingQueue;

public class MemoryQueue<E> extends ConcurrentLinkedQueue<E> implements BlockingQueue<E> {
	private static final long serialVersionUID = 1L;
	private AtomicLong index = new AtomicLong();

	@Override
	public boolean offer(E e) {
		boolean b = super.offer(e);
		if (b) {
			index.getAndIncrement();
		}
		return b;
	}

	@Override
	public E poll() {
		E e = super.poll();
		if (e != null) {
			index.decrementAndGet();
		}
		return e;
	}

	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		return offer(e);
	}

	public void put(E e) throws InterruptedException {
		offer(e);
	}

	public E take() throws InterruptedException {
		if (index.get() == 0) {
			Thread.sleep(1);
		}

		E e = poll();
		while (e == null) {
			Thread.sleep(1);
			e = poll();
		}
		return e;
	}

}

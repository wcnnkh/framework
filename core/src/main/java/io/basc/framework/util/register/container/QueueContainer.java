package io.basc.framework.util.register.container;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Supplier;

import io.basc.framework.util.Elements;
import lombok.NonNull;

public class QueueContainer<E, Q extends Queue<ElementRegistration<E>>> extends CollectionContainer<E, Q>
		implements Queue<E> {

	public QueueContainer(@NonNull Supplier<? extends Q> containerSupplier) {
		super(containerSupplier);
	}

	@Override
	public boolean offer(E e) {
		return registers(Elements.singleton(e), (q, rs) -> {
			rs.forEach((r) -> {
				if (!q.offer(r)) {
					r.cancel();
				}
			});
		}, getPublisher()).isCancelled();
	}

	@Override
	public E remove() {
		return update((q) -> {
			if (q == null) {
				throw new NoSuchElementException();
			}

			ElementRegistration<E> registration = q.remove();
			while (registration.isCancelled()) {
				registration = q.remove();
			}
			batchDeregister(Elements.singleton(registration), getPublisher());
			return registration.getPayload();
		});
	}

	@Override
	public E poll() {
		return update((q) -> {
			if (q == null) {
				return null;
			}

			ElementRegistration<E> registration = q.poll();
			while (registration != null && registration.isCancelled()) {
				registration = q.poll();
			}

			if (registration == null) {
				return null;
			}

			batchDeregister(Elements.singleton(registration), getPublisher());
			return registration.getPayload();
		});
	}

	@Override
	public E element() {
		return read((q) -> {
			if (q == null) {
				throw new NoSuchElementException();
			}

			ElementRegistration<E> registration = q.element();
			while (registration.isCancelled()) {
				registration = q.element();
			}
			return registration.getPayload();
		});
	}

	@Override
	public E peek() {
		return update((q) -> {
			if (q == null) {
				return null;
			}

			ElementRegistration<E> registration = q.peek();
			while (registration != null && registration.isCancelled()) {
				registration = q.peek();
			}

			if (registration == null) {
				return null;
			}

			return registration.getPayload();
		});
	}

}
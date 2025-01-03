package io.basc.framework.util.register.container;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.exchange.event.ChangeEvent;
import io.basc.framework.util.exchange.event.ChangeType;
import io.basc.framework.util.register.PayloadRegistration;

public class TreeSetContainer<E> extends CollectionContainer<E, TreeSet<ElementRegistration<E>>> {
	private volatile Comparator<? super E> comparator;

	public TreeSetContainer() {
		super(TreeSet::new);
	}

	public Comparator<? super E> getComparator() {
		return comparator;
	}

	@Override
	protected TreeSet<ElementRegistration<E>> newContainer() {
		if (comparator != null) {
			return new TreeSet<>(Comparator.comparing(PayloadRegistration::getPayload, comparator));
		}
		return super.newContainer();
	}

	public void setComparator(Comparator<? super E> comparator) {
		if (comparator == this.comparator) {
			return;
		}

		Lock lock = writeLock();
		lock.lock();
		try {
			update((set) -> {
				this.comparator = comparator;
				if (set == null) {
					return false;
				}

				reset();
				TreeSet<ElementRegistration<E>> container = newContainer();
				container.addAll(set);
				set.clear();
				set.addAll(container);
				List<ChangeEvent<E>> events = container.stream()
						.map((e) -> new ChangeEvent<>(e.getPayload(), ChangeType.UPDATE)).collect(Collectors.toList());
				getPublisher().publish(Elements.of(events));
				return true;
			});
		} finally {
			lock.unlock();
		}
	}

}

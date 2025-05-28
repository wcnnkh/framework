package run.soeasy.framework.core.exchange.container.map;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.container.ElementRegistration;
import run.soeasy.framework.core.exchange.container.PayloadRegistration;
import run.soeasy.framework.core.exchange.container.collection.CollectionContainer;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.exchange.event.ChangeType;

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

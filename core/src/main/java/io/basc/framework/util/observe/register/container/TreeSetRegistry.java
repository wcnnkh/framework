package io.basc.framework.util.observe.register.container;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.observe.Publisher;
import io.basc.framework.util.observe.event.ChangeEvent;
import io.basc.framework.util.observe.event.ChangeType;
import io.basc.framework.util.observe.register.PayloadRegistration;
import lombok.NonNull;

public class TreeSetRegistry<E> extends NavigableSetRegistry<E, TreeSet<ElementRegistration<E>>> {
	private Comparator<? super E> comparator;

	public TreeSetRegistry(@NonNull Publisher<? super Elements<ChangeEvent<E>>> changeEventsPublisher) {
		super(TreeSet::new, changeEventsPublisher);
	}

	@Override
	public final Comparator<? super E> comparator() {
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

		Lock lock = getReadWriteLock().writeLock();
		lock.lock();
		try {
			update((set) -> {
				this.comparator = comparator;
				if (set == null) {
					return false;
				}

				TreeSet<ElementRegistration<E>> container = newContainer();
				container.addAll(set);
				set.clear();
				set.addAll(container);
				List<ChangeEvent<E>> events = container.stream()
						.map((e) -> new ChangeEvent<>(e.getPayload(), ChangeType.UPDATE)).collect(Collectors.toList());
				getChangeEventsPublisher().publish(Elements.of(events));
				return true;
			});
		} finally {
			lock.unlock();
		}
	}

	@Override
	public NavigableSet<E> subSet(
			Function<? super TreeSet<ElementRegistration<E>>, ? extends Set<ElementRegistration<E>>> getter) {
		return read((map) -> {
			if (map == null) {
				return Collections.emptyNavigableSet();
			}

			Set<ElementRegistration<E>> set = getter.apply(map);
			if (set == null || set.isEmpty()) {
				return Collections.emptyNavigableSet();
			}

			return set.stream().map((e) -> e.getPayload())
					.collect(Collectors.toCollection(() -> new TreeSet<>(comparator)));
		});
	}

}

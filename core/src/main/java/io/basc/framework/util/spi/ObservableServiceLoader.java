package io.basc.framework.util.spi;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import io.basc.framework.util.CacheableElements;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.event.ChangeEvent;
import io.basc.framework.util.event.ChangeType;
import lombok.NonNull;

public class ObservableServiceLoader<S> extends CacheableElements<S, Collection<S>> {
	private static final long serialVersionUID = 1L;
	private final BiPredicate<? super S, ? super S> equalsPredicate;
	@NonNull
	private final Publisher<? super Elements<ChangeEvent<S>>> eventsPublisher;
	@NonNull
	private final ServiceLoader<? extends S> serviceLoader;

	public ObservableServiceLoader(@NonNull ServiceLoader<? extends S> serviceLoader,
			@NonNull Collector<? super S, ?, Collection<S>> collector,
			@NonNull BiPredicate<? super S, ? super S> equalsPredicate,
			@NonNull Publisher<? super Elements<ChangeEvent<S>>> eventsPublisher) {
		super(serviceLoader, collector);
		this.serviceLoader = serviceLoader;
		this.equalsPredicate = equalsPredicate;
		this.eventsPublisher = eventsPublisher;
	}

	private void onChange(List<S> leftList, List<S> rightList) {
		Elements<ChangeEvent<S>> events;
		if (leftList.isEmpty()) {
			events = Elements.of(leftList).map((e) -> new ChangeEvent<>(e, ChangeType.CREATE));
		} else if (rightList.isEmpty()) {
			events = Elements.of(rightList).map((e) -> new ChangeEvent<>(e, ChangeType.DELETE));
		} else {
			// 移除相同的原因
			Iterator<S> leftIterator = leftList.iterator();
			while (leftIterator.hasNext()) {
				S left = leftIterator.next();
				Iterator<S> rightIterator = rightList.iterator();
				while (rightIterator.hasNext()) {
					S right = rightIterator.next();
					if (equalsPredicate.test(left, right)) {
						// 相同的忽略
						leftIterator.remove();
						rightIterator.remove();
					}
				}
			}

			// 左边剩下的说明被删除了
			Elements<ChangeEvent<S>> leftEvents = Elements.of(leftList)
					.map((e) -> new ChangeEvent<>(e, ChangeType.DELETE));
			// 右边剩下的说明是创建的
			Elements<ChangeEvent<S>> rightEvents = Elements.of(rightList)
					.map((e) -> new ChangeEvent<>(e, ChangeType.CREATE));
			events = leftEvents.concat(rightEvents);
		}
		eventsPublisher.publish(events);
	}

	@Override
	public void reload() {
		try {
			serviceLoader.reload();
		} finally {
			super.reload();
		}
	}

	@Override
	public boolean reload(boolean force) {
		synchronized (this) {
			List<S> leftList = collect(Collectors.toList());
			try {
				return super.reload(force);
			} finally {
				List<S> rightList = collect(Collectors.toList());
				onChange(leftList, rightList);
			}
		}
	}
}

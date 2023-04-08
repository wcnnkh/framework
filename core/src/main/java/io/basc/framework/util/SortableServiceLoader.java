package io.basc.framework.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.support.StandardBroadcastEventDispatcher;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, exclude = "comparator")
public class SortableServiceLoader<S> extends ServiceLoaderRegistry<S> {
	private volatile Comparator<? super S> comparator;
	private final ServiceLoaderRegistry<S> dependencies;

	public SortableServiceLoader() {
		this(new StandardBroadcastEventDispatcher<>());
	}

	public SortableServiceLoader(BroadcastEventDispatcher<ChangeEvent<Elements<ServiceLoader<S>>>> eventDispatcher) {
		super(eventDispatcher);
		this.dependencies = new ServiceLoaderRegistry<>(eventDispatcher);
	}

	public Comparator<? super S> getComparator() {
		return comparator;
	}

	public ServiceLoaderRegistry<S> getDependencies() {
		return dependencies;
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty() && dependencies.isEmpty();
	}

	@Override
	public void reload() {
		try {
			dependencies.reload();
		} finally {
			super.reload();
		}
	}

	@Override
	public Iterator<S> iterator() {
		Comparator<? super S> comparator = getComparator();
		Iterator<S> iterator = new MultiIterator<>(super.iterator(), dependencies.iterator());
		return comparator == null ? iterator : Streams.stream(iterator).sorted(comparator).iterator();
	}

	@Override
	public Stream<S> stream() {
		Comparator<? super S> comparator = getComparator();
		Stream<S> stream = Stream.concat(super.stream(), dependencies.stream());
		return comparator == null ? stream : stream.sorted(comparator);
	}

	public void setComparator(Comparator<? super S> comparator) {
		this.comparator = comparator;
	}
}

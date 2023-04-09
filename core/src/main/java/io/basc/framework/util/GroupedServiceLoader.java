package io.basc.framework.util;

import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.support.StandardBroadcastEventDispatcher;
import io.basc.framework.lang.Nullable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GroupedServiceLoader<S> extends SortableServiceLoader<S> {
	private final SortableServiceLoader<S> first;
	private final SortableServiceLoader<S> last;

	public GroupedServiceLoader() {
		this(new StandardBroadcastEventDispatcher<>());
	}

	public GroupedServiceLoader(BroadcastEventDispatcher<ChangeEvent<Elements<ServiceLoader<S>>>> eventDispatcher) {
		super(eventDispatcher);
		this.first = new SortableServiceLoader<>(eventDispatcher);
		this.last = new SortableServiceLoader<>(eventDispatcher);
	}

	public SortableServiceLoader<S> getFirst() {
		return first;
	}

	public SortableServiceLoader<S> getLast() {
		return last;
	}

	@Override
	public boolean isEmpty() {
		return first.isEmpty() && super.isEmpty() && last.isEmpty();
	}

	@Override
	public Iterator<S> iterator() {
		return new MultiIterator<>(first.iterator(), super.iterator(), last.iterator());
	}

	@Override
	public Stream<S> stream() {
		return Stream.concat(Stream.concat(first.stream(), super.stream()), last.stream());
	}

	public Registration setFirst(@Nullable S service) {
		Registration registration = first.clear();
		return service == null ? registration : registration.and(first.registerService(service));
	}

	public Registration setLast(@Nullable S service) {
		Registration registration = last.clear();
		return service == null ? registration : registration.and(last.registerService(service));
	}
}

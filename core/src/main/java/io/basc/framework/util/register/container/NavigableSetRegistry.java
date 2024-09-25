package io.basc.framework.util.register.container;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.function.Supplier;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.ChangeType;
import lombok.NonNull;

public abstract class NavigableSetRegistry<E, C extends NavigableSet<ElementRegistration<E>>>
		extends SortedSetRegistry<E, C> implements NavigableSet<E> {

	public NavigableSetRegistry(@NonNull Supplier<? extends C> containerSupplier,
			@NonNull Publisher<? super Elements<ChangeEvent<E>>> changeEventsPublisher) {
		super(containerSupplier, changeEventsPublisher);
	}

	@Override
	public abstract NavigableSet<E> subSet(Function<? super C, ? extends Set<ElementRegistration<E>>> getter);

	@Override
	public final E ceiling(E e) {
		return getPayload((set) -> set.ceiling(newElementRegistration(e)));
	}

	@Override
	public final Iterator<E> descendingIterator() {
		// TODO 返回的视图应该支持操作吗？
		return descendingSet().iterator();
	}

	@Override
	public final NavigableSet<E> descendingSet() {
		// TODO 返回的视图应该支持操作吗？
		return subSet((map) -> map.descendingSet());
	}

	@Override
	public final E floor(E e) {
		return getPayload((set) -> set.floor(newElementRegistration(e)));
	}

	@Override
	public final NavigableSet<E> headSet(E toElement, boolean inclusive) {
		return subSet((set) -> set.headSet(newElementRegistration(toElement), inclusive));
	}

	@Override
	public final E higher(E e) {
		return getPayload((set) -> set.higher(newElementRegistration(e)));
	}

	@Override
	public final E lower(E e) {
		return getPayload((set) -> set.lower(newElementRegistration(e)));
	}

	public final E poll(Function<? super C, ? extends ElementRegistration<E>> poller) {
		return getPayload((set) -> {
			ElementRegistration<E> registration = poller.apply(set);
			if (registration == null) {
				return null;
			}

			registration.cancel();
			getChangeEventsPublisher()
					.publish(Elements.singleton(new ChangeEvent<>(registration.getPayload(), ChangeType.DELETE)));
			return registration;
		});
	}

	@Override
	public final E pollFirst() {
		return poll((set) -> set.pollFirst());
	}

	@Override
	public final E pollLast() {
		return poll((set) -> set.pollLast());
	}

	@Override
	public final NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
		return subSet((set) -> set.subSet(newElementRegistration(fromElement), fromInclusive,
				newElementRegistration(toElement), toInclusive));
	}

	@Override
	public final SortedSet<E> subSet(E fromElement, E toElement) {
		return subSet((set) -> set.subSet(newElementRegistration(fromElement), newElementRegistration(toElement)));
	}

	@Override
	public final NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
		return subSet((set) -> set.tailSet(newElementRegistration(fromElement), inclusive));
	}
}

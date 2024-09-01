package io.basc.framework.util.register.container;

import java.util.Collections;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.register.PayloadRegistration;
import lombok.NonNull;

public class TreeSetRegistry<E> extends NavigableSetRegistry<E, TreeSet<ElementRegistration<E>>> {
	@NonNull
	private final Comparator<? super E> comparator;

	public TreeSetRegistry(@NonNull Comparator<? super E> comparator,
			@NonNull EventPublishService<ChangeEvent<E>> eventPublishService) {
		super(() -> new TreeSet<>(Comparator.comparing(PayloadRegistration::getPayload, comparator)),
				eventPublishService);
		this.comparator = comparator;
	}

	@Override
	public final Comparator<? super E> comparator() {
		return comparator;
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

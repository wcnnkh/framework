package io.basc.framework.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.basc.framework.util.CollectionUtils;

public class MultiEventRegistration implements EventRegistration {
	private List<EventRegistration> eventRegistrations;

	public MultiEventRegistration(EventRegistration... registrations) {
		this(Arrays.asList(registrations));
	}

	public MultiEventRegistration(Collection<EventRegistration> eventRegistrations) {
		this.eventRegistrations = Arrays.asList(eventRegistrations.toArray(new EventRegistration[0]));
	}

	public void unregister() {
		if (eventRegistrations != null) {
			for (EventRegistration registration : eventRegistrations) {
				if (registration == null) {
					continue;
				}

				registration.unregister();
			}
		}
	}

	public static <T extends Event> EventRegistration registerListener(EventListener<T> eventListener,
			Collection<? extends EventRegistry<T>> registries) {
		int size = CollectionUtils.isEmpty(registries) ? 0 : registries.size();
		if (size == 0) {
			return EventRegistration.EMPTY;
		}

		List<EventRegistration> registrations = new ArrayList<EventRegistration>(size);
		if (!CollectionUtils.isEmpty(registries)) {
			for (EventRegistry<T> registry : registries) {
				if (registry == null) {
					continue;
				}

				registrations.add(registry.registerListener(eventListener));
			}
		}

		if (registrations.isEmpty()) {
			return EventRegistration.EMPTY;
		}
		return new MultiEventRegistration(registrations.toArray(new EventRegistration[0]));
	}

	@SafeVarargs
	public static <T extends Event> EventRegistration registerListener(EventListener<T> eventListener,
			EventRegistry<T>... registries) {
		return registerListener(eventListener, registries == null ? null : Arrays.asList(registries));
	}

	public static <K, T extends Event> EventRegistration registerListener(K name, EventListener<T> eventListener,
			Collection<? extends NamedEventRegistry<K, T>> registries) {
		int size = CollectionUtils.isEmpty(registries) ? 0 : registries.size();
		if (size == 0) {
			return EventRegistration.EMPTY;
		}

		List<EventRegistration> registrations = new ArrayList<EventRegistration>(size);
		if (!CollectionUtils.isEmpty(registries)) {
			for (NamedEventRegistry<K, T> registry : registries) {
				if (registry == null) {
					continue;
				}

				registrations.add(registry.registerListener(name, eventListener));
			}
		}

		if (registrations.isEmpty()) {
			return EventRegistration.EMPTY;
		}
		return new MultiEventRegistration(registrations.toArray(new EventRegistration[0]));
	}

	@SafeVarargs
	public static <K, T extends Event> EventRegistration registerListener(K name, EventListener<T> eventListener,
			NamedEventRegistry<K, T>... registries) {
		return registerListener(name, eventListener, registries == null ? null : Arrays.asList(registries));
	}
}

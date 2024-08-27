package io.basc.framework.util.observe.container;

import java.util.Collection;
import java.util.function.Supplier;

import io.basc.framework.util.Elements;
import io.basc.framework.util.event.EventDispatcher;
import io.basc.framework.util.event.EventListener;
import io.basc.framework.util.event.EventRegistrationException;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.observe.Observable;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.container.ElementRegistration;
import io.basc.framework.util.register.container.ElementRegistry;
import lombok.NonNull;

public class ObservableCollection<E, C extends Collection<ElementRegistration<E>>> extends ElementRegistry<E, C>
		implements Observable<ChangeEvent<E>> {
	@NonNull
	private final EventDispatcher<ChangeEvent<E>> eventDispatcher;

	public ObservableCollection(@NonNull Supplier<? extends C> containerSupplier,
			@NonNull EventDispatcher<ChangeEvent<E>> eventDispatcher) {
		super(containerSupplier, eventDispatcher);
		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public Registration registerBatchEventsListener(EventListener<Elements<ChangeEvent<E>>> batchEventListener)
			throws EventRegistrationException {
		return eventDispatcher.registerBatchEventsListener(batchEventListener);
	}
}

package io.basc.framework.util.observe.supplier;

import java.util.function.Supplier;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.event.EventDispatcher;
import io.basc.framework.util.event.EventListener;
import io.basc.framework.util.event.EventRegistrationException;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.register.Registration;
import lombok.NonNull;

public class ObservableReloadableSupplier<T> extends ReloadableSupplierObserver<T> implements ObservableSupplier<T> {
	private final EventDispatcher<ChangeEvent<T>> eventDispatcher;

	public ObservableReloadableSupplier(@NonNull Supplier<? extends T> supplier,
			@NonNull EventDispatcher<ChangeEvent<T>> eventDispatcher) {
		super(supplier, eventDispatcher);
		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public Registration registerBatchEventsListener(EventListener<Elements<ChangeEvent<T>>> batchEventsListener)
			throws EventRegistrationException {
		return eventDispatcher.registerBatchEventsListener(batchEventsListener);
	}

}

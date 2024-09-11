package io.basc.framework.observe.container;

import io.basc.framework.observe.PublishService;
import io.basc.framework.observe.UpdateEvent;
import io.basc.framework.util.observe.event.ChangeEvent;
import io.basc.framework.util.observe.register.container.AtomicElementRegistration;
import io.basc.framework.util.register.Registration;
import lombok.NonNull;

public class ObservableElementRegistration<E> extends AtomicElementRegistration<E> {
	@NonNull
	private final PublishService<ChangeEvent<E>> publishService;

	public ObservableElementRegistration(E initialValue, PublishService<ChangeEvent<E>> publishService) {
		super(initialValue);
		this.publishService = publishService;
	}

	public ObservableElementRegistration(ObservableElementRegistration<E> observableElementRegistration) {
		this(observableElementRegistration, observableElementRegistration.publishService);
	}

	private ObservableElementRegistration(@NonNull AtomicElementRegistration<E> elementRegistration,
			PublishService<ChangeEvent<E>> publishService) {
		super(elementRegistration);
		this.publishService = publishService;
	}

	@Override
	public AtomicElementRegistration<E> combine(@NonNull Registration registration) {
		return new ObservableElementRegistration<>(super.combine(registration), this.publishService);
	}

	@Override
	public E setValue(E value) {
		E oldValue = super.setValue(value);
		publishService.publishEvent(new UpdateEvent<>(value, oldValue));
		return oldValue;
	}
}

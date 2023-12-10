package io.basc.framework.observe.register;

import io.basc.framework.observe.ChangeType;
import io.basc.framework.observe.PayloadChangeEvent;

public class RegistryEvent<E> extends PayloadChangeEvent<E> {
	private static final long serialVersionUID = 1L;

	public RegistryEvent(Object source, ChangeType type, E payload) {
		super(source, type, payload);
	}
}

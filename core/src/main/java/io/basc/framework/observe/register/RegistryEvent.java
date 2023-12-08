package io.basc.framework.observe.register;

import io.basc.framework.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RegistryEvent<E> extends Event {
	private static final long serialVersionUID = 1L;
	private final RegistryEventType type;
	private final E element;

	public RegistryEvent(Object source, RegistryEventType type, E element) {
		super(source);
		this.type = type;
		this.element = element;
	}
}

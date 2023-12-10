package io.basc.framework.observe;

import io.basc.framework.util.Assert;

public class ObservableEvent<T> extends PayloadChangeEvent<Changed<T>> {
	private static final long serialVersionUID = 1L;

	public ObservableEvent(Object source, T before, T after) {
		this(source, new Changed<>(before, after));
	}

	public ObservableEvent(Object source, Changed<T> payload) {
		super(source, Assert.requiredArgument(payload != null, "payload", payload.getChangeType()), payload);
	}

}

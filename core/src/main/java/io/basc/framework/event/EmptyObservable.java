package io.basc.framework.event;

public final class EmptyObservable<T> extends NotSupportedObservable<T> {

	public EmptyObservable() {
		super(null);
	}
}
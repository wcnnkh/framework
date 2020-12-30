package scw.event;

public final class EmptyObservable<T> extends NotSupportedObservable<T> {
	
	public EmptyObservable() {
		super(null);
	}
}
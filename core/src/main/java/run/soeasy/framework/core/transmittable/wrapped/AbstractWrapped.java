package run.soeasy.framework.core.transmittable.wrapped;

import run.soeasy.framework.core.transmittable.Inheriter;

public class AbstractWrapped<A, B, I extends Inheriter<A, B>, W> extends Inheritable<A, B, I, W> {
	protected final A capture;

	public AbstractWrapped(W source, I inheriter) {
		super(source, inheriter);
		this.capture = inheriter.capture();
	}
}
package run.soeasy.framework.core.transmittable.wrapped;

import lombok.NonNull;
import run.soeasy.framework.core.Wrapped;
import run.soeasy.framework.core.transmittable.Inheriter;

public class Inheritable<A, B, I extends Inheriter<A, B>, W> extends Wrapped<W> {
	@NonNull
	protected final I inheriter;

	public Inheritable(W source, I inheriter) {
		super(source);
		this.inheriter = inheriter;
	}
}
package run.soeasy.framework.core.transmittable.wrapped;

import java.util.concurrent.Executor;

import run.soeasy.framework.core.transmittable.Inheriter;

public class InhertableExecutor<A, B, I extends Inheriter<A, B>, W extends Executor> extends Inheritable<A, B, I, W>
		implements Executor {

	public InhertableExecutor(W source, I inheriter) {
		super(source, inheriter);
	}

	@Override
	public void execute(Runnable command) {
		source.execute(new WrappedRunnable<>(command, inheriter));
	}
}
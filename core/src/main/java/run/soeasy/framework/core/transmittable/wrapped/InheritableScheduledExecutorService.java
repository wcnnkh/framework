package run.soeasy.framework.core.transmittable.wrapped;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import run.soeasy.framework.core.transmittable.Inheriter;

public class InheritableScheduledExecutorService<A, B, I extends Inheriter<A, B>, W extends ScheduledExecutorService>
		extends InheritableExecutorService<A, B, I, W> implements ScheduledExecutorService {

	public InheritableScheduledExecutorService(W source, I inheriter) {
		super(source, inheriter);
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		return getSource().schedule(new WrappedCallable<>(callable, inheriter), delay, unit);
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		return getSource().schedule(new WrappedRunnable<>(command, inheriter), delay, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		return getSource().scheduleAtFixedRate(new WrappedRunnable<>(command, inheriter), initialDelay, period, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		return scheduleWithFixedDelay(new WrappedRunnable<>(command, inheriter), initialDelay, delay, unit);
	}
}

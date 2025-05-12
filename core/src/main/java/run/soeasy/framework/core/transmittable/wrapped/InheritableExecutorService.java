package run.soeasy.framework.core.transmittable.wrapped;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.transmittable.Inheriter;

public class InheritableExecutorService<A, B, I extends Inheriter<A, B>, W extends ExecutorService>
		extends InhertableExecutor<A, B, I, W> implements ExecutorService {

	public InheritableExecutorService(W source, I inheriter) {
		super(source, inheriter);
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return getSource().awaitTermination(timeout, unit);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		return getSource().invokeAll(CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
				: tasks.stream().map((e) -> new WrappedCallable<>(e, inheriter)).collect(Collectors.toList()));
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		return getSource().invokeAll(
				CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
						: tasks.stream().map((e) -> new WrappedCallable<>(e, inheriter)).collect(Collectors.toList()),
				timeout, unit);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		return getSource().invokeAny(CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
				: tasks.stream().map((e) -> new WrappedCallable<>(e, inheriter)).collect(Collectors.toList()));
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return getSource().invokeAny(
				CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
						: tasks.stream().map((e) -> new WrappedCallable<>(e, inheriter)).collect(Collectors.toList()),
				timeout, unit);
	}

	@Override
	public boolean isShutdown() {
		return getSource().isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return getSource().isTerminated();
	}

	@Override
	public void shutdown() {
		getSource().shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return getSource().shutdownNow();
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return getSource().submit(new WrappedCallable<>(task, inheriter));
	}

	@Override
	public Future<?> submit(Runnable task) {
		return getSource().submit(new WrappedRunnable<>(task, inheriter));
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return getSource().submit(new WrappedRunnable<>(task, inheriter), result);
	}
}
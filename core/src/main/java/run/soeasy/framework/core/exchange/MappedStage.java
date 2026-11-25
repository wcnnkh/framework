package run.soeasy.framework.core.exchange;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class MappedStage<S, T, W extends Stage<S>> implements Stage<T>, SignalWrapper<W> {
	private final W source;
	private final Function<? super S, ? extends T> mapper;

	@Override
	public T get() throws InterruptedException, ExecutionException {
		S value = source.get();
		return mapper.apply(value);
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		S value = source.get();
		return mapper.apply(value);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return SignalWrapper.super.cancel(mayInterruptIfRunning);
	}

	@Override
	public void await() throws InterruptedException, ExecutionException {
		SignalWrapper.super.await();
	}

	@Override
	public void await(long timeout, @NonNull TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		SignalWrapper.super.await(timeout, unit);
	}

	@Override
	public ListenableStage<T> listenable() {
		return new MappedListenableStage<>(source.listenable(), mapper);
	}

	@Override
	public boolean isDone() {
		return SignalWrapper.super.isDone();
	}

	@Override
	public boolean isCancelled() {
		return SignalWrapper.super.isCancelled();
	}
}

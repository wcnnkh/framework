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
class MappedTrace<S, T, W extends Stage<S>> implements Trace<T> {
	private final W source;
	private final Function<? super S, ? extends T> mapper;

	@Override
	public T get() throws InterruptedException, ExecutionException {
		try {
			S value = source.join();
			return mapper.apply(value);
		} catch (Throwable e) {
			throw new ExecutionException(e);
		}
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		try {
			S value = source.join(timeout, unit);
			return mapper.apply(value);
		} catch (TimeoutException e) {
			throw e;
		} catch (Throwable e) {
			throw new ExecutionException(e);
		}
	}

	@Override
	public T join() {
		S value = source.join();
		return mapper.apply(value);
	}

	@Override
	public T join(long timeout, @NonNull TimeUnit unit) throws TimeoutException {
		S value = source.join(timeout, unit);
		return mapper.apply(value);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return source.cancel(mayInterruptIfRunning);
	}

	@Override
	public ListenableTrace<T> listenable() {
		return new MappedListenableTrace<>(source.listenable(), mapper);
	}

	@Override
	public boolean isDone() {
		return source.isDone();
	}

	@Override
	public boolean isCancelled() {
		return source.isCancelled();
	}

	@Override
	public boolean isRollbackSupported() {
		return source.isRollbackSupported();
	}

	@Override
	public Signal rollback() {
		return source.rollback();
	}
}

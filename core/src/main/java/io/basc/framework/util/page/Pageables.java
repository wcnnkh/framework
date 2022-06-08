package io.basc.framework.util.page;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import io.basc.framework.util.XUtils;
import io.basc.framework.util.stream.Processor;

public interface Pageables<K, T> extends Pageable<K, T> {
	Pageables<K, T> jumpTo(K cursorId);

	default Pageables<K, T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException("cursorId=" + getCursorId() + ", nextCursorId=" + getNextCursorId());
		}
		return jumpTo(getNextCursorId());
	}

	default Stream<? extends Pageables<K, T>> pages() {
		Iterator<Pageables<K, T>> iterator = new PageablesIterator<>(this);
		return XUtils.stream(iterator);
	}

	@Override
	default Pageables<K, T> shared() {
		return new SharedPageables<>(this);
	}

	/**
	 * 获取所有的数据
	 * 
	 * @return
	 */
	default Stream<T> streamAll() {
		if (hasNext()) {
			Iterator<T> iterator = new IteratorAll<>(this);
			return XUtils.stream(iterator);
		}
		return stream();
	}

	default Pageable<K, T> all() {
		return new StreamPageable<K, T>(getCursorId(), () -> streamAll(), null);
	}

	default <V> List<Future<V>> invokeAll(ExecutorService executorService, long timeout, TimeUnit timeUnit,
			Processor<? super Pageable<K, T>, ? extends V, ? extends Exception> processor) throws InterruptedException {
		return executorService.invokeAll(PageSupport.toGroupTasks(this, processor), timeout, timeUnit);
	}

	default <V> V invokeAny(ExecutorService executorService, long timeout, TimeUnit timeUnit,
			Processor<? super Pageable<K, T>, ? extends V, ? extends Exception> processor)
			throws InterruptedException, ExecutionException, TimeoutException {
		return executorService.invokeAny(PageSupport.toGroupTasks(this, processor), timeout, timeUnit);
	}

	default <V> List<Future<V>> invokeAll(ExecutorService executorService,
			Processor<? super Pageable<K, T>, ? extends V, ? extends Exception> processor) throws InterruptedException {
		return executorService.invokeAll(PageSupport.toGroupTasks(this, processor));
	}

	default <V> V invokeAny(ExecutorService executorService,
			Processor<? super Pageable<K, T>, ? extends V, ? extends Exception> processor)
			throws InterruptedException, ExecutionException {
		return executorService.invokeAny(PageSupport.toGroupTasks(this, processor));
	}
}

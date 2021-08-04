package scw.util.page;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import scw.util.Wrapper;

public class PageableWrapper<P extends Pageable<K, T>, K, T> extends Wrapper<P> implements Pageable<K, T> {

	public PageableWrapper(P wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Iterator<T> iterator() {
		return wrappedTarget.iterator();
	}

	@Override
	public long getCreateTime() {
		return wrappedTarget.getCreateTime();
	}

	@Override
	public K getCursorId() {
		return wrappedTarget.getCursorId();
	}

	@Override
	public Long getCount() {
		return wrappedTarget.getCount();
	}

	@Override
	public K getNextCursorId() {
		return wrappedTarget.getNextCursorId();
	}

	@Override
	public boolean hasNext() {
		return wrappedTarget.hasNext();
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		wrappedTarget.forEach(action);
	}

	@Override
	public <R> Pageable<K, R> map(Function<? super T, ? extends R> mapper) {
		return wrappedTarget.map(mapper);
	}

	@Override
	public List<T> rows() {
		return wrappedTarget.rows();
	}

	@Override
	public Spliterator<T> spliterator() {
		return wrappedTarget.spliterator();
	}

	@Override
	public Stream<T> stream() {
		return wrappedTarget.stream();
	}

	@Override
	public Pageable<K, T> next(PageableProcessor<K, T> processor) {
		return wrappedTarget.next(processor);
	}
}

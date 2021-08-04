package scw.util.page;

import java.util.function.Function;

public class PageWrapper<P extends Page<T>, T> extends CursorWrapper<P, Long, T> implements Page<T> {

	public PageWrapper(P cursor) {
		super(cursor);
	}

	@Override
	public <R> Page<R> map(Function<? super T, ? extends R> mapper) {
		return wrappedTarget.map(mapper);
	}

	@Override
	public Long getPageNumber() {
		return wrappedTarget.getPageNumber();
	}

	@Override
	public Long getPages() {
		return wrappedTarget.getPages();
	}

	@Override
	public Long getTotal() {
		return wrappedTarget.getTotal();
	}

}

package scw.util.page;

import java.util.function.Function;

public class MapperPage<P extends Page<S>, S, T> extends MapperPageable<P, Long, S, T> implements Page<T> {

	public MapperPage(P cursor, Function<? super S, ? extends T> mapper) {
		super(cursor, mapper);
	}

	@Override
	public long getPages() {
		return wrappedTarget.getPages();
	}

	@Override
	public long getTotal() {
		return wrappedTarget.getPages();
	}

	@Override
	public long getPageNumber() {
		return wrappedTarget.getPageNumber();
	}
}

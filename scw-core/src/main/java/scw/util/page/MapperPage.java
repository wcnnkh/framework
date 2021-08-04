package scw.util.page;

import java.util.function.Function;

public class MapperPage<P extends Page<S>, S, T> extends MapperCursor<P, Long, S, T> implements Page<T> {

	public MapperPage(P cursor, Function<? super S, ? extends T> mapper) {
		super(cursor, mapper);
	}

	@Override
	public Long getPages() {
		return wrappedTarget.getPages();
	}

	@Override
	public Long getTotal() {
		return wrappedTarget.getPages();
	}

	@Override
	public Long getPageNumber() {
		return wrappedTarget.getPageNumber();
	}
}

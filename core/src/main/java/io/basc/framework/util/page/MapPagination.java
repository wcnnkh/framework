package io.basc.framework.util.page;

import java.util.function.Function;

public class MapPagination<M extends Pagination<ST>, ST, T> extends MapPage<M, Long, ST, Long, T>
		implements Pagination<T> {

	public MapPagination(M source, Function<? super ST, T> valueMap) {
		super(source, Function.identity(), valueMap);
	}

	@Override
	public long getPageNumber() {
		return source.getPageNumber();
	}

	@Override
	public long getPages() {
		return source.getPages();
	}

	@Override
	public boolean hasPrevious() {
		return source.hasPrevious();
	}
}

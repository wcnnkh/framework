package io.basc.framework.util.page;

import java.util.function.Function;

public class ConvertiblePagination<M extends Pagination<ST>, ST, T> extends ConvertiblePage<M, Long, ST, Long, T>
		implements Pagination<T> {

	public ConvertiblePagination(M source, Function<? super ST, ? extends T> valueMap) {
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

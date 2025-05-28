package run.soeasy.framework.core.page;

import java.util.function.Function;

import run.soeasy.framework.core.collection.Elements;

public class ConvertiblePage<M extends Page<SK, ST>, SK, ST, K, T> extends ConvertibleCursor<M, SK, ST, K, T>
		implements Page<K, T> {

	public ConvertiblePage(M source, Function<? super SK, ? extends K> cursorIdConverter,
			Function<? super Elements<ST>, ? extends Elements<T>> elementsConverter) {
		super(source, cursorIdConverter, elementsConverter);
	}

	@Override
	public long getTotal() {
		return source.getTotal();
	}

	@Override
	public long getPageSize() {
		return source.getPageSize();
	}
}
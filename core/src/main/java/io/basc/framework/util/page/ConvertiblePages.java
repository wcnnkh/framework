package io.basc.framework.util.page;

import java.util.function.Function;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;

public class ConvertiblePages<M extends Pages<SK, ST>, SK, ST, K, T> extends ConvertiblePage<M, SK, ST, K, T>
		implements Pages<K, T> {
	private final Codec<SK, K> cursorIdCodec;

	public ConvertiblePages(M source, Codec<SK, K> cursorIdCodec,
			Function<? super Elements<ST>, ? extends Elements<T>> elementsConverter) {
		super(source, Assert.requiredArgument(cursorIdCodec != null, "cursorIdCodec", cursorIdCodec)::encode,
				elementsConverter);
		this.cursorIdCodec = cursorIdCodec;
	}

	@Override
	public Pages<K, T> jumpTo(K cursorId, long count) {
		SK targetCursorId = cursorId == null ? null : cursorIdCodec.decode(cursorId);
		Pages<SK, ST> pages = source.jumpTo(targetCursorId);
		return new ConvertiblePages<>(pages, cursorIdCodec, elementsConverter);
	}

}

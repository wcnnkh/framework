package run.soeasy.framework.core.page;

import java.util.function.Function;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.Elements;

public class ConvertiblePageable<M extends Pageable<SK, ST>, SK, ST, K, T> extends ConvertiblePage<M, SK, ST, K, T>
		implements Pageable<K, T> {
	private final Codec<SK, K> cursorIdCodec;

	public ConvertiblePageable(M source, Codec<SK, K> cursorIdCodec,
			Function<? super Elements<ST>, ? extends Elements<T>> elementsConverter) {
		super(source, Assert.requiredArgument(cursorIdCodec != null, "cursorIdCodec", cursorIdCodec)::encode,
				elementsConverter);
		this.cursorIdCodec = cursorIdCodec;
	}

	@Override
	public Pageable<K, T> jumpTo(K cursorId, long count) {
		SK targetCursorId = cursorId == null ? null : cursorIdCodec.decode(cursorId);
		Pageable<SK, ST> pages = source.jumpTo(targetCursorId);
		return new ConvertiblePageable<>(pages, cursorIdCodec, elementsConverter);
	}

}
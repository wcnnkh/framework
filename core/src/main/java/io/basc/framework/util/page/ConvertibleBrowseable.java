package io.basc.framework.util.page;

import java.util.function.Function;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;

public class ConvertibleBrowseable<M extends Browseable<SK, ST>, SK, ST, K, T>
		extends ConvertibleCursor<M, SK, ST, K, T> implements Browseable<K, T> {
	protected final Codec<SK, K> cursorIdCodec;

	public ConvertibleBrowseable(M source, Codec<SK, K> cursorIdCodec,
			Function<? super Elements<ST>, ? extends Elements<T>> elementsConverter) {
		super(source, Assert.requiredArgument(cursorIdCodec != null, "elementsConverter", cursorIdCodec)::encode,
				elementsConverter);
		this.cursorIdCodec = cursorIdCodec;
	}

	@Override
	public Browseable<K, T> jumpTo(K cursorId) {
		SK targetCursorId = cursorId == null ? null : cursorIdCodec.decode(cursorId);
		Browseable<SK, ST> pageables = source.jumpTo(targetCursorId);
		return new ConvertibleBrowseable<>(pageables, cursorIdCodec, elementsConverter);
	}

}

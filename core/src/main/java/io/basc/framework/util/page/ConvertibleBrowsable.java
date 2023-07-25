package io.basc.framework.util.page;

import java.util.function.Function;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;

public class ConvertibleBrowsable<M extends Browsable<SK, ST>, SK, ST, K, T>
		extends ConvertibleCursor<M, SK, ST, K, T> implements Browsable<K, T> {
	protected final Codec<SK, K> cursorIdCodec;

	public ConvertibleBrowsable(M source, Codec<SK, K> cursorIdCodec,
			Function<? super Elements<ST>, ? extends Elements<T>> elementsConverter) {
		super(source, Assert.requiredArgument(cursorIdCodec != null, "elementsConverter", cursorIdCodec)::encode,
				elementsConverter);
		this.cursorIdCodec = cursorIdCodec;
	}

	@Override
	public Browsable<K, T> jumpTo(K cursorId) {
		SK targetCursorId = cursorId == null ? null : cursorIdCodec.decode(cursorId);
		Browsable<SK, ST> pageables = source.jumpTo(targetCursorId);
		return new ConvertibleBrowsable<>(pageables, cursorIdCodec, elementsConverter);
	}

}

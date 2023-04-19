package io.basc.framework.util.page;

import java.util.function.Function;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ObjectUtils;

public class ConvertiblePageables<M extends Pageables<SK, ST>, SK, ST, K, T>
		extends ConvertiblePageable<M, SK, ST, K, T> implements Pageables<K, T> {
	protected final Codec<SK, K> cursorIdCodec;

	public ConvertiblePageables(M source, Codec<SK, K> cursorIdCodec,
			Function<? super Elements<ST>, ? extends Elements<T>> elementsConverter) {
		super(source, Assert.requiredArgument(cursorIdCodec, "elementsConverter", ObjectUtils::isNotEmpty)::encode,
				elementsConverter);
		this.cursorIdCodec = cursorIdCodec;
	}

	@Override
	public Pageables<K, T> jumpTo(K cursorId) {
		SK targetCursorId = cursorId == null ? null : cursorIdCodec.decode(cursorId);
		Pageables<SK, ST> pageables = source.jumpTo(targetCursorId);
		return new ConvertiblePageables<>(pageables, cursorIdCodec, elementsConverter);
	}

}

package io.basc.framework.util.codec;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NestedMultipleEncoder<E, W extends MultipleEncoder<E>> implements MultipleEncoderWrapper<E, W> {
	@NonNull
	private final W source;
	private final int count;

	@Override
	public E encode(E source) throws EncodeException {
		return getSource().encode(source, count);
	}

	@Override
	public E encode(E source, int count) throws EncodeException {
		return getSource().encode(source, count * this.count);
	}
}

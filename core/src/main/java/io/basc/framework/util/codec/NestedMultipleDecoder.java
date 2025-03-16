package io.basc.framework.util.codec;

import io.basc.framework.util.codec.MultipleDecoder.MultipleDecoderWrapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NestedMultipleDecoder<D, W extends MultipleDecoder<D>> implements MultipleDecoderWrapper<D, W> {
	@NonNull
	private final W source;
	private final int count;

	@Override
	public D decode(D source) throws DecodeException {
		return getSource().decode(source, count);
	}

	@Override
	public D decode(D source, int count) throws DecodeException {
		return getSource().decode(source, count * this.count);
	}
}

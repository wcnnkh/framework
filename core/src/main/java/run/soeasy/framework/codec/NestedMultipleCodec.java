package run.soeasy.framework.codec;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NestedMultipleCodec<T, W extends MultipleCodec<T>> implements MultipleCodecWrapper<T, W> {
	@NonNull
	private final W source;
	private final int count;

	@Override
	public T encode(T source) throws EncodeException {
		return getSource().encode(source, count);
	}

	@Override
	public T decode(T source) throws DecodeException {
		return getSource().decode(source, count);
	}

	@Override
	public T encode(T encode, int count) throws EncodeException {
		return getSource().encode(encode, count * this.count);
	}

	@Override
	public T decode(T source, int count) throws DecodeException {
		return getSource().decode(source, count * this.count);
	}

}

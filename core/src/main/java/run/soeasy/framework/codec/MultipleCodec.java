package run.soeasy.framework.codec;

public interface MultipleCodec<T> extends Codec<T, T>, MultipleEncoder<T>, MultipleDecoder<T> {
	@Override
	default MultipleCodec<T> multiple(int count) {
		return new NestedMultipleCodec<>(this, count);
	}
}

package run.soeasy.framework.codec;

public interface MultipleDecoderWrapper<D, W extends MultipleDecoder<D>>
		extends MultipleDecoder<D>, DecoderWrapper<D, D, W> {
	@Override
	default D decode(D source, int count) throws DecodeException {
		return getSource().decode(source, count);
	}

	@Override
	default MultipleDecoder<D> multiple(int count) {
		return getSource().multiple(count);
	}
}
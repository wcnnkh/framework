package run.soeasy.framework.codec;

enum IdentityCodec implements UnaryCodec<Object> {
	INSTANCE;

	@Override
	public UnaryCoder<Object> encoder() {
		return IdentityCoder.INSTANCE;
	}

	@Override
	public UnaryCoder<Object> decoder() {
		return IdentityCoder.INSTANCE;
	}

}

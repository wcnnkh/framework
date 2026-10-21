package run.soeasy.framework.codec.io;

import run.soeasy.framework.codec.UnaryCodec;

public interface StringCodec extends UnaryCodec<String> {
	@Override
	StringCoder encoder();

	@Override
	StringCoder decoder();
}

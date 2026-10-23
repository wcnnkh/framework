package run.soeasy.framework.codec.io;

import run.soeasy.framework.codec.UnaryCodec;

public interface StringCodec extends UnaryCodec<String>, Formatter<String> {
	@Override
	StringEncoder encoder();

	@Override
	StringDecoder decoder();
}

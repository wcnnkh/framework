package run.soeasy.framework.codec.string;

import run.soeasy.framework.codec.MultipleCodec;

public interface StringCodec extends MultipleCodec<CharSequence>, FromStringCodec<CharSequence>,
		ToStringCodec<CharSequence>, StringEncoder, StringDecoder {
}

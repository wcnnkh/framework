package run.soeasy.framework.codec.string;

import run.soeasy.framework.codec.Codec;

public interface ToStringCodec<D> extends Codec<D, CharSequence>, ToStringEncoder<D>, FromStringDecoder<D> {
}

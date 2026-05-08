package run.soeasy.framework.codec.string;

import run.soeasy.framework.codec.Codec;

public interface FromStringCodec<E> extends Codec<CharSequence, E>, FromStringEncoder<E>, ToStringDecoder<E> {
}

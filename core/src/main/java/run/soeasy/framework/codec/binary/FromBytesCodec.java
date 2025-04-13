package run.soeasy.framework.codec.binary;

import run.soeasy.framework.codec.Codec;

public interface FromBytesCodec<E> extends Codec<byte[], E>, FromBytesEncoder<E>, ToBytesDecoder<E> {

}

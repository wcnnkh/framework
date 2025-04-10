package run.soeasy.framework.util.codec.binary;

import run.soeasy.framework.util.codec.Codec;

public interface FromBytesCodec<E> extends Codec<byte[], E>, FromBytesEncoder<E>, ToBytesDecoder<E> {

}

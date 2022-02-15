package io.basc.framework.codec.support;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.decode.ToBytesDecoder;
import io.basc.framework.codec.encode.FromBytesEncoder;

public interface FromBytesCodec<E> extends Codec<byte[], E>, FromBytesEncoder<E>, ToBytesDecoder<E> {

}

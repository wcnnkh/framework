package io.basc.framework.util.codec.support;

import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.codec.decode.ToBytesDecoder;
import io.basc.framework.util.codec.encode.FromBytesEncoder;

public interface FromBytesCodec<E> extends Codec<byte[], E>, FromBytesEncoder<E>, ToBytesDecoder<E> {

}

package run.soeasy.framework.util.codec.support;

import run.soeasy.framework.util.codec.Codec;
import run.soeasy.framework.util.codec.decode.ToBytesDecoder;
import run.soeasy.framework.util.codec.encode.FromBytesEncoder;

public interface FromBytesCodec<E> extends Codec<byte[], E>, FromBytesEncoder<E>, ToBytesDecoder<E> {

}

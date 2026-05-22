package run.soeasy.framework.codec.stream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.Decoder;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.transfer.StreamTransferrer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;

/**
 * GZIP解码器，实现对GZIP压缩的二进制数据进行解码（解压）操作，
 * 同时实现{@link StreamTransferrer}接口以支持流式传输解压功能。
 *
 * <p>该类提供了默认实例{@link #DEFAULT}，适用于大多数GZIP解压场景，
 * 支持直接处理{@link GZIPInputStream}或自动将普通输入流包装为GZIP输入流。
 *
 * @author soeasy.run
 * @see GZIPInputStream
 */
@RequiredArgsConstructor
public class GzipDecoder implements Decoder<byte[], byte[]>, StreamTransferrer {
    /**
     * GzipDecoder的默认实例，可直接用于GZIP解压操作
     */
    public static final GzipDecoder DEFAULT = new GzipDecoder();
    @NonNull
    private final ThrowingFunction<? super InputStream, ? extends GZIPInputStream, ? extends IOException> mapper;

    public GzipDecoder() {
        this(GZIPInputStream::new);
    }

    @Override
    public final byte[] decode(byte[] source) throws CodecException {
        return toBinary(ByteBuffer.wrap(source));
    }

    @Override
    public void transfer(@NonNull InputStream input, @NonNull OutputStream output) throws IOException {
        input = input instanceof GZIPInputStream ? IOUtils.ignoreClose(input) : mapper.apply(IOUtils.ignoreClose(input));
        try {
            IOUtils.binary().transfer(input, output);
        } finally {
            input.close();
        }
    }
}
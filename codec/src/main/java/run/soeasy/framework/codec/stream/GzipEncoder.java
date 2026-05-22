package run.soeasy.framework.codec.stream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.Encoder;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.transfer.StreamTransferrer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP编码器，实现对二进制数据的GZIP压缩（编码）操作，
 * 同时实现{@link StreamTransferrer}接口以支持流式传输压缩功能，
 * 可将输入流数据压缩后传输到输出流或缓冲区消费者。
 *
 * <p>该类提供默认实例{@link #DEFAULT}，适用于大多数GZIP压缩场景，
 * 支持直接处理{@link GZIPInputStream}或对普通输入流进行GZIP压缩。
 *
 * @author soeasy.run
 * @see Encoder
 * @see GZIPOutputStream
 */
@RequiredArgsConstructor
public class GzipEncoder implements StreamTransferrer, Encoder<byte[], byte[]> {
    /**
     * GzipEncoder的默认实例，可直接用于GZIP压缩操作
     */
    public static final GzipEncoder DEFAULT = new GzipEncoder();
    @NonNull
    private final ThrowingFunction<? super OutputStream, ? extends GZIPOutputStream, ? extends IOException> mapper;

    public GzipEncoder() {
        this(GZIPOutputStream::new);
    }

    @Override
    public final byte[] encode(byte[] source) throws CodecException {
        return toBinary(ByteBuffer.wrap(source));
    }

    @Override
    public void transfer(@NonNull InputStream input, @NonNull OutputStream output) throws IOException {
        output = output instanceof GZIPOutputStream ? IOUtils.ignoreClose(output) : mapper.apply(IOUtils.ignoreClose(output));
        try {
            IOUtils.binary().transfer(input, output);
        } finally {
            output.close();
        }
    }
}
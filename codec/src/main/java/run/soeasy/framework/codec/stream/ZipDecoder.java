package run.soeasy.framework.codec.stream;

import lombok.Getter;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipInputStream;

/**
 * ZIP解码器，实现对ZIP压缩的二进制数据进行解码（解压）操作，
 * 同时实现{@link StreamTransferrer}接口以支持流式传输解压功能，
 * 依赖指定的字符集处理ZIP条目名称等文本信息。
 *
 * <p>该类提供了使用UTF-8字符集的默认实例{@link #UTF_8}，适用于大多数ZIP解压场景，
 * 支持直接处理{@link ZipInputStream}或自动将普通输入流包装为ZIP输入流。
 *
 * @author soeasy.run
 * @see Decoder
 * @see StreamTransferrer
 * @see ZipInputStream
 */
@Getter
@RequiredArgsConstructor
public class ZipDecoder implements Decoder<byte[], byte[]>, StreamTransferrer {

    public static final ZipDecoder DEFAULT = new ZipDecoder();
    /**
     * 使用UTF-8字符集的ZipDecoder默认实例，适用于大多数ZIP解压场景
     */
    public static final ZipDecoder UTF_8 = new ZipDecoder(StandardCharsets.UTF_8);

    @NonNull
    private final ThrowingFunction<? super InputStream, ? extends ZipInputStream, ? extends IOException> mapper;

    private ZipDecoder() {
        this(ZipInputStream::new);
    }

    public ZipDecoder(Charset charset) {
        this((input) -> new ZipInputStream(input, charset));
    }

    @Override
    public final byte[] decode(byte[] source) throws CodecException {
        return toBinary(ByteBuffer.wrap(source));
    }

    @Override
    public void transfer(@NonNull InputStream input, @NonNull OutputStream output) throws IOException {
        input = input instanceof ZipInputStream ? IOUtils.ignoreClose(input) : mapper.apply(IOUtils.ignoreClose(input));
        try {
            IOUtils.binary().transfer(input, output);
        } finally {
            input.close();
        }
    }
}
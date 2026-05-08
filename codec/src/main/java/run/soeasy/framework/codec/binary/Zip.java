package run.soeasy.framework.codec.binary;

/**
 * ZIP格式编解码器，继承自{@link TransferrerCodec}，专门用于ZIP格式的二进制数据编解码，
 * 组合{@link ZipEncoder}（编码/压缩）和{@link ZipDecoder}（解码/解压）实现完整的ZIP处理能力。
 * 
 * <p>该类为final类，不可继承，确保ZIP编解码逻辑的稳定性。提供默认的UTF-8字符集实例{@link #UTF_8}，
 * 适用于大多数需要ZIP压缩和解压的场景，简化ZIP格式数据的处理流程。
 * 
 * @author soeasy.run
 * @see TransferrerCodec
 * @see ZipEncoder
 * @see ZipDecoder
 */
public final class Zip extends TransferrerCodec<ZipEncoder, ZipDecoder> {
    /**
     * 使用UTF-8字符集的默认ZIP编解码器实例，可直接用于ZIP格式的压缩与解压操作
     */
    public static final Zip UTF_8 = new Zip(ZipEncoder.UTF_8, ZipDecoder.UTF_8);

    /**
     * 构造ZIP编解码器（指定ZIP编码器和解码器）
     * 
     * @param encodeTransferrer ZIP编码器（用于ZIP压缩）
     * @param decodeTransferrer ZIP解码器（用于ZIP解压）
     */
    public Zip(ZipEncoder encodeTransferrer, ZipDecoder decodeTransferrer) {
        super(encodeTransferrer, decodeTransferrer);
    }
}
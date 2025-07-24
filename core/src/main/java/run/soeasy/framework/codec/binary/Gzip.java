package run.soeasy.framework.codec.binary;

import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP格式编解码器，实现{@link BinaryCodec}接口，提供GZIP压缩与解压缩功能，
 * 支持流式处理大文件数据，并通过临时文件策略优化内存占用。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>压缩编码：将原始数据转换为GZIP格式字节流</li>
 * <li>解压缩解码：将GZIP格式数据还原为原始字节流</li>
 * <li>大文件处理：通过分段读取支持GB级文件压缩/解压缩</li>
 * <li>临时文件策略：编码时使用临时文件缓存中间结果，避免内存溢出</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>日志压缩：压缩应用日志文件以减少存储占用</li>
 * <li>网络传输：压缩HTTP响应数据降低带宽消耗</li>
 * <li>文件归档：创建GZIP格式的备份文件</li>
 * <li>大数据处理：压缩分布式文件系统中的数据块</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see BinaryCodec
 * @see GZIPInputStream
 * @see GZIPOutputStream
 */
public final class Gzip extends TransferrerCodec<GzipEncoder, GzipDecoder> {
	public static final Gzip DEFAULT = new Gzip();

	public Gzip() {
		this(GzipEncoder.DEFAULT, GzipDecoder.DEFAULT);
	}

	public Gzip(GzipEncoder encodeTransferrer, GzipDecoder decodeTransferrer) {
		super(encodeTransferrer, decodeTransferrer);
	}

}
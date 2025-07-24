package run.soeasy.framework.codec.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.MultipleEncoder;
import run.soeasy.framework.io.BinaryTransferrer;

/**
 * 字节数组编码器接口，整合多维度编码能力，支持从不同数据源读取字节并执行编码操作， 提供流式处理、多次编码及组合扩展功能，适用于复杂二进制数据处理场景。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>多源编码：支持从字节数组、{@link InputStream}、文件等数据源读取字节</li>
 * <li>多次编码：通过{@link #encode(byte[], int)}实现指定次数的连续编码</li>
 * <li>流式处理：基于输入流/输出流的分段编码设计，支持大文件高效处理</li>
 * <li>组合扩展：通过父接口组合其他编码器形成多级编码流程</li>
 * </ul>
 * 
 * <p>
 * <b>典型场景：</b>
 * <ul>
 * <li>多层数据处理："压缩→加密→Base64编码"的链式处理流程</li>
 * <li>大文件编码：GB级视频文件的分段压缩与编码</li>
 * <li>安全数据传输：网络数据的多次加密与校验</li>
 * <li>格式转换场景：二进制配置文件的多轮格式转换</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see FromBinaryEncoder
 * @see ToBinaryEncoder
 * @see MultipleEncoder
 */
@FunctionalInterface
public interface BinaryEncoder extends FromBinaryEncoder<byte[]>, ToBinaryEncoder<byte[]>, MultipleEncoder<byte[]> {

	/**
	 * 对字节数组执行编码操作（实现{@link FromBinaryEncoder}）。
	 * <p>
	 * 直接调用父接口编码逻辑，由实现类处理字节数组到目标类型的转换。
	 * 
	 * @param source 待编码的字节数组，不可为null
	 * @return 编码后的字节数组
	 * @throws CodecException 编码逻辑失败时抛出
	 */
	@Override
	default byte[] encode(byte[] source) throws CodecException {
		return FromBinaryEncoder.super.encode(source);
	}

	@Override
	default void encode(byte[] source, @NonNull OutputStream target) throws IOException, CodecException {
		byte[] temp = this.encode(source);
		target.write(temp);
	}

	@Override
	default byte[] encode(@NonNull InputStream source, int bufferSize) throws IOException, CodecException {
		return getEncodeTransferrer().toByteArray(source, bufferSize);
	}

	BinaryTransferrer getEncodeTransferrer();

	@Override
	default BinaryEncoder multiple(int count) {
		return new BinaryEncoderWrapper<BinaryEncoder>() {

			@Override
			public int getEncodeMultiple() {
				return count;
			}

			@Override
			public BinaryEncoder getSource() {
				return BinaryEncoder.this;
			}
		};
	}
}
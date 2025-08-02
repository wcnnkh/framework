package run.soeasy.framework.codec.binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.MultipleDecoder;
import run.soeasy.framework.io.BinaryTransferrer;
import run.soeasy.framework.io.BufferConsumer;

/**
 * 字节数组解码器接口，整合多维度解码能力，支持从不同数据源读取字节并执行解码操作， 提供流式处理、多次解码及组合扩展功能，适用于复杂二进制数据处理场景。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>多源解码：支持从字节数组、{@link InputStream}、文件等数据源读取字节</li>
 * <li>多次解码：通过{@link MultipleDecoder#decode(Object, int)}实现指定次数的连续解码</li>
 * <li>流式处理：基于输入流/输出流的分段解码设计，支持大文件高效处理</li>
 * <li>组合扩展：通过{@link #fromDecoder}和{@link #toDecoder}组合多级解码器形成处理链</li>
 * </ul>
 * 
 * <p>
 * <b>典型场景：</b>
 * <ul>
 * <li>多层数据处理："Base64解码→解密→解压缩"的链式处理流程</li>
 * <li>大文件解码：GB级压缩文件的分段解压与处理</li>
 * <li>安全数据解析：网络传输的加密数据的多层解密</li>
 * <li>格式转换场景：二进制配置文件的多轮格式转换</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see FromBinaryDecoder
 * @see ToBinaryDecoder
 * @see MultipleDecoder
 */
@FunctionalInterface
public interface BinaryDecoder extends FromBinaryDecoder<byte[]>, ToBinaryDecoder<byte[]>, MultipleDecoder<byte[]> {

	/**
	 * 将字节数组解码为字节数组（实现{@link FromBinaryDecoder}接口）。
	 * <p>
	 * 执行流程：
	 * <ol>
	 * <li>将字节数组转换为{@link ByteArrayInputStream}</li>
	 * <li>调用{@link #decode(InputStream, int)}执行流式解码</li>
	 * <li>将结果写入ByteArrayOutputStream并转换为字节数组</li>
	 * </ol>
	 * 
	 * @param source 待解码的字节数组，可为null（返回null）
	 * @return 解码后的字节数组，source为null时返回null
	 * @throws CodecException 解码逻辑失败时抛出
	 */
	@Override
	default byte[] decode(byte[] source) throws CodecException {
		return FromBinaryDecoder.super.decode(source);
	}
	
	@Override
	default <S extends Throwable> void decode(byte[] source,
			@NonNull BufferConsumer<? super byte[], ? extends S> target) throws CodecException, IOException, S {
		byte[] temp = decode(source);
		target.accept(temp, 0, temp.length);
	}

	@Override
	default byte[] decode(@NonNull InputStream source, int bufferSize) throws IOException, CodecException {
		return getDecodeTransferrer().toByteArray(source, bufferSize);
	}

	BinaryTransferrer getDecodeTransferrer();

	/**
	 * 组合前置解码器形成新的解码流程（装饰器模式）。
	 * <p>
	 * 处理流程示意图：
	 * 
	 * <pre>
	 * byte[] → [前置解码器] → byte[] → [当前解码器] → byte[]
	 * </pre>
	 * 
	 * 典型应用：前置解码器处理Base64编码，当前解码器解压缩数据。
	 * 
	 * @param decoder 前置解码器，不可为null
	 * @return 组合后的新解码器
	 */
	default BinaryDecoder fromDecoder(BinaryDecoder decoder) {
		return new ChainBinaryDecoder() {

			@Override
			public BinaryDecoder getToDecoder() {
				return BinaryDecoder.this;
			}

			@Override
			public BinaryDecoder getFromDecoder() {
				return decoder;
			}
		};
	}

	/**
	 * 组合后置解码器形成新的解码流程（装饰器模式）。
	 * <p>
	 * 处理流程示意图：
	 * 
	 * <pre>
	 * byte[] → [当前解码器] → byte[] → [后置解码器] → byte[]
	 * </pre>
	 * 
	 * 典型应用：当前解码器解压缩数据，后置解码器解密数据。
	 * 
	 * @param decoder 后置解码器，不可为null
	 * @return 组合后的新解码器
	 */
	default BinaryDecoder toDecoder(BinaryDecoder decoder) {
		return new ChainBinaryDecoder() {

			@Override
			public BinaryDecoder getToDecoder() {
				return decoder;
			}

			@Override
			public BinaryDecoder getFromDecoder() {
				return BinaryDecoder.this;
			}
		};
	}

	@Override
	default BinaryDecoder multiple(int count) {
		return new BinaryDecoderWrapper<BinaryDecoder>() {

			@Override
			public int getDecodeMultiple() {
				return count;
			}

			@Override
			public BinaryDecoder getSource() {
				return BinaryDecoder.this;
			}
		};
	}
}
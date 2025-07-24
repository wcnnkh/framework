package run.soeasy.framework.codec.binary;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.Decoder;
import run.soeasy.framework.io.InputSource;

/**
 * 字节数组解码器接口，扩展自{@link Decoder}，用于将字节数组或输入流解码为目标类型，
 * 提供流式解码功能，支持从多种数据源读取数据，适用于二进制数据解析场景。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>多源解码：支持从字节数组、{@link InputStream}、{@link File}等数据源读取字节</li>
 * <li>流式处理：基于输入流的分段解码设计，支持大文件高效处理</li>
 * <li>链式组合：通过{@link #toDecoder(Decoder)}组合其他解码器形成多级解码流程</li>
 * <li>类型安全：将字节数组解码为泛型目标类型{D}，确保类型转换安全</li>
 * </ul>
 * 
 * <p>
 * <b>典型场景：</b>
 * <ul>
 * <li>二进制协议解析：解析HTTP/Redis协议的字节流为协议对象</li>
 * <li>加密数据处理：解析AES加密的字节数组为原始数据</li>
 * <li>文件格式转换：将ZIP文件字节流解码为文件对象</li>
 * <li>多级解码流程：组合Base64解码器和Protobuf解码器解析数据</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <D> 解码后的目标数据类型
 * @see Decoder
 */
@FunctionalInterface
public interface FromBinaryDecoder<D> extends Decoder<byte[], D> {

	/**
	 * 将字节数组解码为目标类型（实现{@link Decoder}接口）。
	 * <p>
	 * 执行流程：
	 * <ol>
	 * <li>校验输入字节数组非null（通过@NonNull保证）</li>
	 * <li>将字节数组转换为{@link ByteArrayInputStream}</li>
	 * <li>调用{@link #decode(InputStream, int)}执行流式解码</li>
	 * </ol>
	 * 
	 * @param source 待解码的字节数组，不可为null
	 * @return 解码后的目标类型对象
	 * @throws CodecException 解码逻辑失败时抛出，内部封装IOException
	 */
	@Override
	default D decode(@NonNull byte[] source) throws CodecException {
		try {
			return decode(new ByteArrayInputStream(source), source.length);
		} catch (IOException e) {
			throw new DecodeException("Internal IO exception", e);
		}
	}

	/**
	 * 从输入源读取数据并解码为目标类型（使用指定缓冲区）。
	 * <p>
	 * 执行流程：
	 * <ol>
	 * <li>从输入源获取输入流</li>
	 * <li>调用{@link #decode(InputStream, int)}执行解码</li>
	 * <li>确保输入流被正确关闭（使用try-finally）</li>
	 * </ol>
	 * 
	 * @param source     输入源对象，不可为null
	 * @param bufferSize 解码缓冲区大小（建议≥1024）
	 * @return 解码后的目标类型对象
	 * @throws IOException    输入源获取流或关闭流失败时抛出
	 * @throws CodecException 解码逻辑失败时抛出
	 */
	default D decode(@NonNull InputSource source, int bufferSize) throws IOException, CodecException {
		InputStream inputStream = source.getInputStream();
		try {
			return decode(inputStream, bufferSize);
		} finally {
			inputStream.close();
		}
	}

	/**
	 * 从输入流读取数据并解码为目标类型（核心解码方法）。
	 * <p>
	 * 实现类需定义具体解码逻辑，将输入流的字节数据转换为目标类型， 建议采用分段读取策略以优化大文件处理的内存占用。
	 * 
	 * @param source     待解码的输入流，不可为null
	 * @param bufferSize 解码缓冲区大小（&gt;0）
	 * @return 解码后的目标类型对象
	 * @throws IOException    输入流读取失败时抛出（如文件不存在、网络中断）
	 * @throws CodecException 解码逻辑失败时抛出（如数据格式不支持）
	 */
	D decode(@NonNull InputStream source, int bufferSize) throws IOException, CodecException;

	/**
	 * 组合后置解码器形成新的解码流程（装饰器模式）。
	 * <p>
	 * 新解码流程示意图：
	 * 
	 * <pre>
	 * byte[] → [当前解码器] → D → [后置解码器] → T
	 * </pre>
	 * 
	 * 典型应用：当前解码器将byte[]解码为JSON对象，后置解码器将JSON转为POJO对象。
	 * 
	 * @param <T>     后置解码器的目标类型
	 * @param decoder 后置解码器，不可为null
	 * @return 组合后的新解码器，不可为null
	 */
	@Override
	default <T> FromBinaryDecoder<T> toDecoder(Decoder<D, T> decoder) {
		return new ChainFromBinaryDecoder<D, T>() {

			@Override
			public Decoder<D, T> getToDecoder() {
				return decoder;
			}

			@Override
			public FromBinaryDecoder<D> getFromDecoder() {
				return FromBinaryDecoder.this;
			}
		};
	}
}
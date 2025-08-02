package run.soeasy.framework.codec.binary;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.Encoder;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.InputSource;

/**
 * 从字节数组编码器接口，扩展自{@link Encoder}，用于将字节数组或输入流编码为目标类型，
 * 提供流式编码和验证功能，支持从多种数据源读取字节数据，适用于二进制数据处理和验证场景。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>多源编码：支持从字节数组、{@link InputStream}、{@link File}等数据源读取字节并编码</li>
 * <li>流式处理：基于输入流的分段编码设计，支持GB级大文件高效处理</li>
 * <li>结果验证：通过{@link #test(byte[], Object)}方法实现编码结果与预期目标的一致性校验</li>
 * <li>链式组合：通过{@link #toEncoder(Encoder)}组合其他编码器形成多级编码流程</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>二进制协议解析：将Socket字节流编码为协议对象（如HTTP/Redis协议解析）</li>
 * <li>文件格式转换：解析JPEG图片字节流并提取EXIF元数据</li>
 * <li>加密数据处理：验证AES解密后的字节数组编码结果是否正确</li>
 * <li>配置文件解析：将二进制配置文件编码为应用配置对象</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <E> 编码结果的目标数据类型
 * @see Encoder
 */
@FunctionalInterface
public interface FromBinaryEncoder<E> extends Encoder<byte[], E> {

	/**
	 * 将字节数组编码为目标类型（实现{@link Encoder}接口）。
	 * <p>
	 * 执行流程：
	 * <ol>
	 * <li>校验输入字节数组非null（通过lombok的@NonNull保证）</li>
	 * <li>将字节数组转换为{@link ByteArrayInputStream}</li>
	 * <li>调用{@link #encode(InputStream, int)}执行流式编码</li>
	 * </ol>
	 * 
	 * @param source 待编码的字节数组，不可为null
	 * @return 编码后的目标类型对象
	 * @throws DecodeException 编码逻辑失败时抛出（内部封装可能的IOException）
	 */
	@Override
	default E encode(@NonNull byte[] source) throws CodecException {
		try {
			return encode(new ByteArrayInputStream(source), source.length);
		} catch (IOException e) {
			throw new DecodeException("Internal IO exception", e);
		}
	}

	/**
	 * 从输入流读取数据并编码为目标类型（核心编码方法）。
	 * <p>
	 * 实现类需定义具体编码逻辑，将输入流的字节数据转换为目标类型， 建议采用分段读取策略以优化大文件处理的内存占用。
	 * 
	 * @param source     待编码的输入流，不可为null
	 * @param bufferSize 缓冲区大小（建议≥1024）
	 * @return 编码后的目标类型对象
	 * @throws IOException     输入流读取失败时抛出（如文件不存在、网络中断）
	 * @throws CodecException 编码逻辑失败时抛出（如数据格式不支持、编码算法异常）
	 */
	E encode(@NonNull InputStream source, int bufferSize) throws IOException, CodecException;

	/**
	 * 从输入源读取数据并编码为目标类型（支持自定义输入源）。
	 * <p>
	 * 执行流程：
	 * <ol>
	 * <li>从输入源获取输入流</li>
	 * <li>调用{@link #encode(InputStream, int)}执行编码</li>
	 * <li>确保输入流被正确关闭（使用try-finally）</li>
	 * </ol>
	 * 
	 * @param source     输入源对象，不可为null
	 * @param bufferSize 缓冲区大小
	 * @return 编码后的目标类型对象
	 * @throws IOException     输入源获取流或关闭流失败时抛出
	 * @throws CodecException 编码逻辑失败时抛出
	 */
	default E encode(@NonNull InputSource source, int bufferSize) throws IOException, CodecException {
		InputStream inputStream = source.getInputStream();
		try {
			return encode(inputStream, bufferSize);
		} finally {
			inputStream.close();
		}
	}

	/**
	 * 组合后置编码器形成新的编码流程（装饰器模式）。
	 * <p>
	 * 新编解码流程示意图：
	 * 
	 * <pre>
	 * byte[] → [当前编码器] → E → [后置编码器] → T
	 * </pre>
	 * 
	 * 典型应用：当前编码器将byte[]编码为JSON对象，后置编码器将JSON转为POJO对象。
	 * 
	 * @param <T>     后置编码器的目标类型
	 * @param encoder 后置编码器，不可为null
	 * @return 组合后的新编码器，不可为null
	 */
	@Override
	default <T> FromBinaryEncoder<T> toEncoder(Encoder<E, T> encoder) {
		return new ChainFromBinaryEncoder<E, T>() {

			@Override
			public Encoder<E, T> getToEncoder() {
				return encoder;
			}

			@Override
			public FromBinaryEncoder<E> getFromEncoder() {
				return FromBinaryEncoder.this;
			}

		};
	}

	/**
	 * 验证输入流数据编码结果是否与预期目标相等。
	 * <p>
	 * 执行逻辑：
	 * <ol>
	 * <li>使用指定缓冲区大小编码输入流</li>
	 * <li>通过{@link ObjectUtils#equals}比较编码结果与目标对象</li>
	 * <li>支持null目标对象的比较（编码结果为null时返回true）</li>
	 * </ol>
	 * 
	 * @param source     待验证的输入流，不可为null
	 * @param bufferSize 缓冲区大小
	 * @param target     预期目标对象
	 * @return 编码结果与目标相等返回true，否则返回false
	 * @throws CodecException 编码过程中发生逻辑错误时抛出
	 * @throws IOException     输入流读取失败时抛出
	 */
	default boolean test(@NonNull InputStream source, int bufferSize, E target) throws CodecException, IOException {
		return ObjectUtils.equals(encode(source, bufferSize), target);
	}

	/**
	 * 验证输入源数据编码结果是否与预期目标相等。
	 * <p>
	 * 执行流程：
	 * <ol>
	 * <li>从输入源获取输入流</li>
	 * <li>调用{@link #test(InputSource, int, Object)}执行验证</li>
	 * <li>确保输入流被正确关闭（无论是否发生异常）</li>
	 * </ol>
	 * 
	 * @param source     输入源对象，不可为null
	 * @param bufferSize 缓冲区大小
	 * @param target     预期目标对象
	 * @return 输入流存在且编码结果相等返回true，否则返回false
	 * @throws CodecException 编码过程中发生逻辑错误时抛出
	 * @throws IOException     输入源获取流或关闭流失败时抛出
	 */
	default boolean test(@NonNull InputSource source, int bufferSize, E target) throws CodecException, IOException {
		InputStream inputStream = source.getInputStream();
		try {
			return test(inputStream, bufferSize, target);
		} finally {
			inputStream.close();
		}
	}

	@Override
	default boolean test(byte[] source, E encode) throws CodecException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(source);
		try {
			return test(inputStream, source.length, encode);
		} catch (IOException e) {
			throw new EncodeException("Internal IO exception", e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}
}
package run.soeasy.framework.codec.binary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.Encoder;
import run.soeasy.framework.codec.format.Base64;
import run.soeasy.framework.codec.format.HexCodec;
import run.soeasy.framework.codec.security.MessageDigestEncoder;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.FileUtils;
import run.soeasy.framework.io.OutputSource;
import run.soeasy.framework.io.Resource;

/**
 * 字节数组编码器接口，扩展自{@link Encoder}，用于将泛型数据类型{D}编码为字节数组，
 * 支持链式组合Base64、十六进制、MD5、SHA1等格式转换编码器，提供流式处理和文件操作能力， 适用于二进制数据生成、加密处理和网络传输等场景。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>类型转换：将泛型数据{D}编码为标准字节数组{@code byte[]}</li>
 * <li>格式组合：通过默认方法快速组合Base64/Hex/MD5/SHA1编码器</li>
 * <li>流式处理：支持直接编码到{@link OutputStream}的分段写入</li>
 * <li>文件操作：通过{@link OutputSource}实现文件系统直接写入</li>
 * <li>消费者模式：通过{@link BufferConsumer}处理编码结果（支持异常传递）</li>
 * </ul>
 * 
 * <p>
 * <b>典型应用场景：</b>
 * <ul>
 * <li>对象序列化：将Java对象编码为字节数组（配合自定义序列化实现）</li>
 * <li>加密流程组合："数据编码→MD5哈希→Base64转换"的链式处理</li>
 * <li>文件生成：直接生成二进制文件（如图片、视频、可执行程序）</li>
 * <li>网络传输：将数据编码为字节数组用于Socket/HTTP传输</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <D> 待编码的源数据类型
 * @see Encoder
 * @see Base64
 * @see HexCodec
 * @see MD5
 * @see SHA1
 */
@FunctionalInterface
public interface ToBinaryEncoder<D> extends Encoder<D, byte[]> {

	/**
	 * 将源数据编码为字节数组（实现{@link Encoder}核心方法）。
	 * <p>
	 * 执行流程：
	 * <ol>
	 * <li>创建字节数组输出流收集编码结果</li>
	 * <li>调用{@link #encode(Object, OutputStream)}执行流式编码</li>
	 * <li>处理可能的IO异常并封装为编码异常</li>
	 * <li>返回输出流转换后的字节数组</li>
	 * </ol>
	 * 
	 * @param source 待编码的源数据，不可为null
	 * @return 编码后的字节数组，不可为null
	 * @throws EncodeException 编码逻辑失败或IO错误时抛出
	 */
	@Override
	default byte[] encode(D source) throws CodecException {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try {
			encode(source, target::write);
		} catch (IOException e) {
			throw new EncodeException("Internal IO exception", e);
		}
		return target.toByteArray();
	}

	/**
	 * 将源数据编码并通过消费者处理结果（支持异常传递）。
	 * <p>
	 * 执行逻辑：
	 * <ol>
	 * <li>若消费者为OutputStream，直接调用{@link #encode(Object, OutputStream)}</li>
	 * <li>否则创建临时文件，先编码到临时文件再读取</li>
	 * <li>通过try-finally确保临时文件被删除</li>
	 * </ol>
	 * 
	 * @param source 待编码的源数据，不可为null
	 * @param target 结果消费者，不可为null
	 * @param <E>    消费者可能抛出的异常类型
	 * @throws IOException     IO操作失败时抛出
	 * @throws CodecException 编码逻辑失败时抛出
	 * @throws E               消费者处理异常时抛出
	 */
	default <E extends Throwable> void encode(D source, @NonNull BufferConsumer<? super byte[], ? extends E> target)
			throws IOException, CodecException, E {
		if (target instanceof OutputStream) {
			encode(source, (OutputStream) target);
		} else {
			File templateFile = File.createTempFile(getClass().getSimpleName(), ".encode");
			try {
				this.encode(source, Resource.forFile(templateFile));
				FileUtils.copy(templateFile, target);
			} finally {
				templateFile.delete();
			}
		}
	}

	/**
	 * 将源数据编码并写入输出源（支持文件系统等自定义输出实现）。
	 * <p>
	 * 资源管理流程：
	 * 
	 * <pre>
	 * try {
	 * 	OutputStream outputStream = target.getOutputStream();
	 * 	encode(source, outputStream);
	 * } finally {
	 * 	outputStream.close();
	 * }
	 * </pre>
	 * 
	 * @param source 待编码的源数据，不可为null
	 * @param target 目标输出源，不可为null
	 * @throws CodecException 编码逻辑失败时抛出
	 * @throws IOException     输出源操作失败时抛出
	 */
	default void encode(D source, @NonNull OutputSource target) throws IOException, CodecException {
		OutputStream outputStream = target.getOutputStream();
		try {
			encode(source, outputStream);
		} finally {
			outputStream.close();
		}
	}

	/**
	 * 将源数据编码并写入输出流（核心编码方法，由实现类定义具体逻辑）。
	 * <p>
	 * 实现类需定义从泛型数据{D}到字节数组的转换逻辑， 建议采用分段写入策略以优化大对象编码的内存占用。
	 * 
	 * @param source 待编码的源数据，不可为null
	 * @param target 目标输出流，不可为null
	 * @throws IOException     输出流写入失败时抛出
	 * @throws CodecException 编码逻辑失败时抛出
	 */
	void encode(D source, @NonNull OutputStream target) throws IOException, CodecException;

	/**
	 * 组合Base64编码器形成新的编码流程（装饰器模式）。
	 * <p>
	 * 执行流程示意图：
	 * 
	 * <pre>
	 * D → [当前编码器] → byte[] → [Base64编码器] → String
	 * </pre>
	 * 
	 * 等价于调用{@code toEncoder(Base64.DEFAULT)}的语法糖。
	 * 
	 * @return 组合后的Base64编码器，不可为null
	 */
	default Encoder<D, String> toBase64() {
		return toEncoder(Base64.DEFAULT);
	}

	/**
	 * 组合十六进制编码器形成新的编码流程（装饰器模式）。
	 * <p>
	 * 执行流程示意图：
	 * 
	 * <pre>
	 * D → [当前编码器] → byte[] → [十六进制编码器] → String
	 * </pre>
	 * 
	 * 等价于调用{@code toEncoder(HexCodec.DEFAULT)}的语法糖。
	 * 
	 * @return 组合后的十六进制编码器，不可为null
	 */
	default Encoder<D, String> toHex() {
		return toEncoder(HexCodec.DEFAULT);
	}

	/**
	 * 组合MD5哈希编码器形成新的编码流程（装饰器模式）。
	 * <p>
	 * 执行流程示意图：
	 * 
	 * <pre>
	 * D → [当前编码器] → byte[] → [MD5编码器] → String（32位十六进制）
	 * </pre>
	 * 
	 * 等价于调用{@code toEncoder(MD5.DEFAULT)}的语法糖。
	 * 
	 * @return 组合后的MD5编码器，不可为null
	 */
	default Encoder<D, String> toMD5() {
		return toEncoder(MessageDigestEncoder.MD5.toHex());
	}

	/**
	 * 组合SHA1哈希编码器形成新的编码流程（装饰器模式）。
	 * <p>
	 * 执行流程示意图：
	 * 
	 * <pre>
	 * D → [当前编码器] → byte[] → [SHA1编码器] → String（40位十六进制）
	 * </pre>
	 * 
	 * @return 组合后的SHA1编码器，不可为null
	 */
	default Encoder<D, String> toSHA1() {
		return toEncoder(MessageDigestEncoder.SHA1.toHex());
	}
}
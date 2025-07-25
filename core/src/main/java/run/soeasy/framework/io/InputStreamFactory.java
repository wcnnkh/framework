package run.soeasy.framework.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 输入流工厂接口，用于创建具有特定处理流程的{@link InputStream}实例， 并提供将输入流转换为{@link Reader}的功能。
 * 该接口扩展了{@link ReaderFactory}，支持通过流水线处理输入操作， 并提供解码转换、通道适配等高级功能。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>流水线处理：通过{@link Pipeline}接口支持链式处理输入操作</li>
 * <li>解码支持：提供多种解码方法，支持字符集指定和自定义解码逻辑</li>
 * <li>资源管理：自动关闭输入流资源，避免内存泄漏</li>
 * <li>通道适配：支持将输入流转换为{@link ReadableByteChannel}</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>文件读取：创建带解码的文件输入流</li>
 * <li>网络输入：构建带缓冲或加密的输入流管道</li>
 * <li>数据转换：将字节流转换为字符流并应用解码</li>
 * <li>通道操作：将输入流适配为NIO通道进行异步读取</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <I> 输入流类型
 * @see InputStream
 * @see ReaderFactory
 * @see InputStreamPipeline
 */
@FunctionalInterface
public interface InputStreamFactory<I extends InputStream> extends ReaderFactory<Reader> {

	/**
	 * 为输入流添加指定字符集的解码转换。
	 * <p>
	 * 该方法返回一个新的输入流工厂，将原始输入流通过指定的字符集 转换为目标Reader类型，使用{@link InputStreamReader}实现转换。
	 * 
	 * @param charset 字符集，不可为null
	 * @return 带字符集解码的输入流工厂
	 */
	default InputStreamFactory<I> decode(@NonNull Charset charset) {
		return new DecodedInputStreamFactory<>(this, charset, (e) -> new InputStreamReader(e, charset));
	}

	/**
	 * 为输入流添加指定字符集名称的解码转换。
	 * <p>
	 * 该方法返回一个新的输入流工厂，使用指定的字符集名称 将输入流转换为{@link InputStreamReader}。
	 * 
	 * @param charsetName 字符集名称，不可为null
	 * @return 带字符集解码的输入流工厂
	 */
	default InputStreamFactory<I> decode(@NonNull String charsetName) {
		return new DecodedInputStreamFactory<>(this, charsetName, (e) -> new InputStreamReader(e, charsetName));
	}

	/**
	 * 为输入流添加自定义解码转换。
	 * <p>
	 * 该方法返回一个新的输入流工厂，将原始输入流通过指定的解码器函数 转换为目标Reader类型，支持自定义的解码逻辑。
	 * 
	 * @param decoder 解码转换函数，不可为null
	 * @param <T>     目标Reader类型
	 * @return 带自定义解码的输入流工厂
	 */
	default <T extends Reader> InputStreamFactory<I> decode(
			@NonNull ThrowingFunction<? super I, ? extends T, IOException> decoder) {
		return new DecodedInputStreamFactory<>(this, null, decoder);
	}

	/**
	 * 获取默认的输入流实例，该实例基于工厂定义的流水线处理。
	 * <p>
	 * 此方法返回一个{@link InputStreamPipeline}实例，它将工厂提供的 输入流管道应用于所有读取操作，实现统一的处理流程。
	 * 
	 * @return 基于流水线处理的输入流实例
	 * @throws IOException 如果创建输入流过程中发生I/O错误
	 * @see InputStreamPipeline
	 */
	default InputStream getInputStream() throws IOException {
		return new InputStreamPipeline(getInputStreamPipeline());
	}

	/**
	 * 获取用于创建输入流的流水线。
	 * <p>
	 * 实现类必须提供此方法的具体实现，返回一个{@link Pipeline}实例， 该实例定义了如何创建和处理特定类型的输入流。
	 * 
	 * @return 输入流流水线，不可为null
	 * @throws IOException 如果获取流水线过程中发生I/O错误
	 */
	@NonNull
	Pipeline<I, IOException> getInputStreamPipeline();

	/**
	 * 获取用于创建Reader的流水线，默认将输入流转换为Reader。
	 * <p>
	 * 该方法将输入流流水线映射为Reader流水线，使用{@link InputStreamReader} 实现字节流到字符流的转换，并确保关闭时释放资源。
	 * 
	 * @return Reader流水线
	 */
	@Override
	default @NonNull Pipeline<Reader, IOException> getReaderPipeline() {
		return getInputStreamPipeline().map((e) -> (Reader) new InputStreamReader(e)).onClose((e) -> e.close());
	}

	/**
	 * 判断输入流是否已解码。
	 * <p>
	 * 默认为false，表示未应用解码转换。子类可重写此方法以提供解码状态。
	 * 
	 * @return true表示已解码，false表示未解码
	 */
	default boolean isDecoded() {
		return false;
	}

	/**
	 * 将输入流转换为可读字节通道。
	 * <p>
	 * 该方法使用{@link Channels#newChannel(InputStream)}
	 * 将当前输入流转换为{@link ReadableByteChannel}， 适用于需要NIO通道操作的场景。
	 * 
	 * @return 可读字节通道
	 * @throws IOException 如果转换过程中发生I/O错误
	 */
	default ReadableByteChannel readableChannel() throws IOException {
		return Channels.newChannel(getInputStream());
	}

	/**
	 * 将输入流内容转换为字节数组。
	 * <p>
	 * 该方法读取整个输入流并将其转换为字节数组，自动处理资源关闭。 注意：对于大文件，可能会导致内存问题，建议使用流式处理方法。
	 * 
	 * @return 输入流内容的字节数组表示
	 * @throws IOException 如果读取过程中发生I/O错误
	 */
	default byte[] toByteArray() throws IOException {
		InputStream input = getInputStream();
		try {
			return IOUtils.toByteArray(input);
		} finally {
			input.close();
		}
	}

	/**
	 * 将输入流内容传输到指定文件。
	 * <p>
	 * 该方法将当前输入流的全部内容传输到目标文件，自动处理 资源的打开和关闭，确保数据传输完成后输入流被正确释放。
	 * 
	 * @param dest 目标文件，不可为null
	 * @throws IOException           如果传输过程中发生I/O错误
	 * @throws IllegalStateException 如果目标文件无法写入
	 */
	default void transferTo(@NonNull File dest) throws IOException, IllegalStateException {
		InputStream input = getInputStream();
		try {
			Files.copy(input, dest.toPath());
		} finally {
			input.close();
		}
	}

	/**
	 * 将输入流内容传输到指定的输出流工厂。
	 * <p>
	 * 该方法将当前输入流的全部内容传输到目标输出流，自动处理 资源的打开和关闭，确保数据传输完成后所有资源被正确释放。
	 * 
	 * @param dest 目标输出流工厂，不可为null
	 * @return 传输的字节数
	 * @throws IOException 如果传输过程中发生I/O错误
	 */
	default <R extends OutputStream> long transferTo(@NonNull OutputStreamFactory<? extends R> dest)
			throws IOException {
		InputStream input = getInputStream();
		try {
			OutputStream out = dest.getOutputStream();
			try {
				return IOUtils.transferTo(input, out::write);
			} finally {
				out.close();
			}
		} finally {
			input.close();
		}
	}

	/**
	 * 将输入流内容传输到指定路径。
	 * <p>
	 * 该方法将当前输入流的全部内容传输到目标路径，自动处理 资源的打开和关闭，确保数据传输完成后输入流被正确释放。
	 * 
	 * @param dest 目标路径，不可为null
	 * @throws IOException 如果传输过程中发生I/O错误
	 */
	default void transferTo(@NonNull Path dest) throws IOException {
		InputStream input = getInputStream();
		try {
			Files.copy(input, dest);
		} finally {
			input.close();
		}
	}
}
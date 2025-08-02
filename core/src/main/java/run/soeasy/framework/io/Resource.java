package run.soeasy.framework.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 资源的定义
 * 
 * 统一抽象可读写的输入输出资源，集成{@link InputSource}、{@link OutputSource}接口功能，
 * 提供对文件、路径等资源的统一操作方式。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>统一抽象：屏蔽不同资源类型差异（文件、路径、内存等）</li>
 * <li>双向操作：同时支持读取({@link InputSource})和写入({@link OutputSource})</li>
 * <li>编码转换：内置{@code codec}方法支持字符集转换</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see InputSource
 * @see OutputSource
 */
public interface Resource extends InputSource, OutputSource {
	/**
	 * 返回一个不存在的资源
	 * 
	 * @return 一个单例
	 */
	public static Resource nonexistent() {
		return NonexistentResource.NONEXISTENT_RESOURCE;
	}

	/**
	 * 根据文件创建资源实例
	 * 
	 * @param file 不可为null
	 * @return 文件资源实例
	 */
	public static Resource forFile(@NonNull File file) {
		return new FileResource() {

			@Override
			public File getFile() {
				return file;
			}
		};
	}

	/**
	 * 根据路径创建资源实例
	 * 
	 * @param path 不可为null
	 * @return 路径资源实例
	 */
	public static Resource forPath(@NonNull Path path) {
		return new PathResource() {

			@Override
			public Path getPath() {
				return path;
			}
		};
	}

	/**
	 * 获取资源名称，默认返回类名
	 * 
	 * @return 资源名称
	 */
	default String getName() {
		return getClass().getSimpleName();
	}

	/**
	 * 是否已打开。如果为true则表示此资源已经打开，多次获取可能拿到同样的流
	 * 
	 * @return true表示已打开，false表示未打开
	 */
	default boolean isOpen() {
		return false;
	}

	/**
	 * 内容长度
	 * 
	 * @return -1表示未知
	 * @throws IOException 当获取长度时发生I/O错误
	 */
	default long contentLength() throws IOException {
		return -1;
	}

	/**
	 * 获取资源描述
	 * 
	 * @return 资源描述字符串
	 */
	default String getDescription() {
		return getClass().getName();
	}

	/**
	 * 判断资源是否存在
	 * 
	 * @return true表示资源存在，false表示不存在
	 */
	default boolean exists() {
		return isReadable() || isWritable();
	}

	/**
	 * 最后一次个性记录
	 * 
	 * @return 最后修改时间戳（毫秒）
	 * @throws IOException 当获取修改时间时发生I/O错误
	 */
	long lastModified() throws IOException;

	/**
	 * 是否可读
	 * 
	 * @return true表示可读，false表示不可读
	 */
	boolean isReadable();

	/**
	 * 是否可写
	 * 
	 * @return true表示可写，false表示不可写
	 */
	boolean isWritable();

	/**
	 * 为资源添加指定字符集的解码转换
	 * 
	 * @param charset 字符集，不可为null
	 * @return 带字符集解码的资源
	 */
	@Override
	default Resource decode(@NonNull Charset charset) {
		return codec(charset);
	}

	/**
	 * 为资源添加指定字符集名称的解码转换
	 * 
	 * @param charsetName 字符集名称，不可为null
	 * @return 带字符集解码的资源
	 */
	@Override
	default Resource decode(@NonNull String charsetName) {
		return codec(charsetName);
	}

	/**
	 * 为资源添加指定字符集的编码转换
	 * 
	 * @param charset 字符集，不可为null
	 * @return 带字符集编码的资源
	 */
	@Override
	default Resource encode(Charset charset) {
		return codec(charset);
	}

	/**
	 * 为资源添加指定字符集名称的编码转换
	 * 
	 * @param charsetName 字符集名称，不可为null
	 * @return 带字符集编码的资源
	 */
	@Override
	default Resource encode(String charsetName) {
		return codec(charsetName);
	}

	/**
	 * 为资源添加指定字符集的编解码转换
	 * 
	 * @param charset 字符集，不可为null
	 * @return 带字符集编解码的资源
	 */
	default Resource codec(@NonNull Charset charset) {
		return new CodedResource<>(this, charset, (e) -> new OutputStreamWriter(e, charset),
				(e) -> new InputStreamReader(e, charset));
	}

	/**
	 * 为资源添加指定字符集名称的编解码转换
	 * 
	 * @param charsetName 字符集名称，不可为null
	 * @return 带字符集编解码的资源
	 */
	default Resource codec(String charsetName) {
		return new CodedResource<>(this, charsetName, (e) -> new OutputStreamWriter(e, charsetName),
				(e) -> new InputStreamReader(e, charsetName));
	}

	/**
	 * 为资源添加自定义编解码转换
	 * 
	 * @param encoder 编码转换函数（输出流→Writer），不可为null
	 * @param decoder 解码转换函数（输入流→Reader），不可为null
	 * @return 带自定义编解码的资源
	 */
	default Resource codec(@NonNull ThrowingFunction<? super OutputStream, ? extends Writer, IOException> encoder,
			@NonNull ThrowingFunction<? super InputStream, ? extends Reader, IOException> decoder) {
		return new CodedResource<>(this, null, encoder, decoder);
	}

	/**
	 * 重命名资源并返回新资源实例
	 * 
	 * @param name 新名称，不可为null
	 * @return 重命名后的资源实例
	 */
	default Resource rename(String name) {
		return new RenamedResource<>(this, name);
	}
}
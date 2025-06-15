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
import run.soeasy.framework.io.watch.Variable;

/**
 * 资源的定义
 * 
 * @author shuchaowen
 *
 */
public interface Resource extends InputSource, OutputSource, Variable {
	public static final String EXTENSION_SEPARATOR = ".";
	public static final String FOLDER_SEPARATOR = "/";

	public static String getName(String path) {
		if (path == null) {
			return null;
		}
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
	}

	/**
	 * Extract the filename extension from the given path, e.g. "mypath/myfile.txt"
	 * -&gt; "txt".
	 * 
	 * @param path the file path (may be {@code null})
	 * @return the extracted filename extension, or {@code null} if none
	 */
	public static String getExtension(String path) {
		if (path == null) {
			return null;
		}

		int extIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		if (extIndex == -1) {
			return null;
		}
		int folderIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (folderIndex > extIndex) {
			return null;
		}
		return path.substring(extIndex + 1);
	}

	/**
	 * 返回一个不存在的资源
	 * 
	 * @return 一个单例
	 */
	public static Resource nonexistent() {
		return NonexistentResource.NONEXISTENT_RESOURCE;
	}

	public static Resource forFile(File file) {
		return new FileResource(file);
	}

	public static Resource forPath(Path path) {
		return new PathResource(path);
	}

	default String getName() {
		return getClass().getSimpleName();
	}

	/**
	 * 是否已打开。如果为true则表示此资源已经打开，多次获取可能拿到同样的流
	 * 
	 * @return
	 */
	default boolean isOpen() {
		return false;
	}

	/**
	 * 内容长度
	 * 
	 * @return -1表示未知
	 * @throws IOException
	 */
	default long contentLength() throws IOException {
		return -1;
	}

	/**
	 * 获取资源描述
	 * 
	 * @return
	 */
	default String getDescription() {
		return getClass().getName();
	}

	default boolean exists() {
		return isReadable() || isWritable();
	}

	/**
	 * 最后一次个性记录
	 */
	@Override
	default long lastModified() throws IOException {
		return 0;
	}

	/**
	 * 是否可读
	 * 
	 * @return
	 */
	boolean isReadable();

	/**
	 * 是否可写
	 * 
	 * @return
	 */
	boolean isWritable();

	@Override
	default Resource decode(@NonNull Charset charset) {
		return codec(charset);
	}

	@Override
	default Resource decode(@NonNull String charsetName) {
		return codec(charsetName);
	}

	@Override
	default Resource encode(Charset charset) {
		return codec(charset);
	}

	@Override
	default Resource encode(String charsetName) {
		return codec(charsetName);
	}

	default Resource codec(@NonNull Charset charset) {
		return new CodedResource<>(this, charset, (e) -> new OutputStreamWriter(e, charset),
				(e) -> new InputStreamReader(e, charset));
	}

	default Resource codec(String charsetName) {
		return new CodedResource<>(this, charsetName, (e) -> new OutputStreamWriter(e, charsetName),
				(e) -> new InputStreamReader(e, charsetName));
	}

	default Resource codec(@NonNull ThrowingFunction<? super OutputStream, ? extends Writer, IOException> encoder,
			@NonNull ThrowingFunction<? super InputStream, ? extends Reader, IOException> decoder) {
		return new CodedResource<>(this, null, encoder, decoder);
	}

	default Resource rename(String name) {
		return new RenamedResource<>(this, name);
	}
}

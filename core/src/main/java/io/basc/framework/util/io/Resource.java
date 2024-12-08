package io.basc.framework.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

import io.basc.framework.util.Pipeline;
import io.basc.framework.util.watch.Variable;
import lombok.NonNull;

public interface Resource extends InputStreamFactory<InputStream>, Variable {
	public static class DecodeResource<W extends Resource> extends MappedInputStreamFactory<InputStream, Reader, W>
			implements ResourceWrapper<W> {

		public DecodeResource(@NonNull W source,
				@NonNull Pipeline<? super InputStream, ? extends Reader, ? extends IOException> pipeline) {
			super(source, pipeline);
		}
	}

	public static class CharsetResource<W extends Resource> extends DecodeResource<W> implements CharsetCapable {
		private final Object charset;

		public CharsetResource(@NonNull W source, @NonNull String charsetName) {
			super(source, (is) -> new InputStreamReader(is, charsetName));
			this.charset = charsetName;
		}

		public CharsetResource(@NonNull W source, @NonNull Charset charset) {
			super(source, (is) -> new InputStreamReader(is, charset));
			this.charset = charset;
		}

		@Override
		public String getCharsetName() {
			if (charset instanceof String) {
				return (String) charset;
			}
			return CharsetCapable.super.getCharsetName();
		}

		@Override
		public Charset getCharset() {
			if (charset instanceof Charset) {
				return (Charset) charset;
			}
			return Charset.forName(String.valueOf(charset));
		}
	}

	@FunctionalInterface
	public static interface ResourceWrapper<W extends Resource>
			extends Resource, InputStreamFactoryWrapper<InputStream, W> {
		@Override
		default long contentLength() throws IOException {
			return getSource().contentLength();
		}

		@Override
		default Resource createRelative(String relativePath) throws IOException {
			return getSource().createRelative(relativePath);
		}

		@Override
		default boolean exists() {
			return getSource().exists();
		}

		@Override
		default String getDescription() {
			return getSource().getDescription();
		}

		@Override
		default File getFile() throws IOException, FileNotFoundException {
			return getSource().getFile();
		}

		@Override
		default String getName() {
			return getSource().getName();
		}

		@Override
		default URI getURI() throws IOException {
			return getSource().getURI();
		}

		@Override
		default URL getURL() throws IOException {
			return getSource().getURL();
		}

		@Override
		default boolean isFile() {
			return getSource().isFile();
		}

		@Override
		default boolean isReadable() {
			return getSource().isReadable();
		}

		@Override
		default long lastModified() throws IOException {
			return getSource().lastModified();
		}

		@Override
		default Resource decode(
				@NonNull Pipeline<? super InputStream, ? extends Reader, ? extends IOException> pipeline) {
			return getSource().decode(pipeline);
		}

		@Override
		default Resource encode(@NonNull Charset charset) {
			return getSource().encode(charset);
		}

		@Override
		default Resource encode(@NonNull String charsetName) {
			return getSource().encode(charsetName);
		}
	}

	long contentLength() throws IOException;

	Resource createRelative(String relativePath) throws IOException;

	default Resource decode(@NonNull Pipeline<? super InputStream, ? extends Reader, ? extends IOException> pipeline) {
		return new DecodeResource<>(this, pipeline);
	}

	default Resource encode(@NonNull Charset charset) {
		return new CharsetResource<>(this, charset);
	}

	default Resource encode(@NonNull String charsetName) {
		return new CharsetResource<>(this, charsetName);
	}

	/**
	 * 是否存在
	 * 
	 * @return
	 */
	boolean exists();

	String getDescription();

	File getFile() throws IOException, FileNotFoundException;

	/**
	 * 获取资源名称，如果是文件那应该是文件名
	 * 
	 * @return
	 */
	String getName();

	URI getURI() throws IOException;

	URL getURL() throws IOException;

	default boolean isFile() {
		return false;
	}

	/**
	 * 是否可读,比如一个目录是不可读的，或没有可读权限
	 * 
	 * @return
	 */
	default boolean isReadable() {
		return exists();
	}
}

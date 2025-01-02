package io.basc.framework.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import io.basc.framework.util.Pipeline;
import io.basc.framework.util.Function;
import io.basc.framework.util.alias.Named;
import io.basc.framework.util.io.watch.Variable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * 一个资源的定义
 * 
 * @author shuchaowen
 *
 */
public interface Resource extends InputStreamFactory<InputStream>, OutputStreamFactory<OutputStream>, Variable, Named {

	public static class CharsetResource<W extends Resource> extends CodecResource<W>
			implements CharsetOutputStreamFactory<OutputStream, W>, CharsetInputStreamFactory<InputStream, W> {
		private final Object charset;

		public CharsetResource(@NonNull W source, Charset charset) {
			super(source, (e) -> new OutputStreamWriter(e, charset), (e) -> new InputStreamReader(e, charset));
			this.charset = charset;
		}

		public CharsetResource(@NonNull W source, String charsetName) {
			super(source, (e) -> new OutputStreamWriter(e, charsetName), (e) -> new InputStreamReader(e, charsetName));
			this.charset = charsetName;
		}

		@Override
		public Charset getCharset() {
			if (charset instanceof Charset) {
				return (Charset) charset;
			}
			return Charset.forName(String.valueOf(charset));
		}

		@Override
		public String getCharsetName() {
			if (charset instanceof String) {
				return (String) charset;
			}
			return CharsetInputStreamFactory.super.getCharsetName();
		}
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class CodecResource<W extends Resource>
			extends StandardEncodeOutputStreamFactory<OutputStream, Writer, W> implements ResourceWrapper<W>,
			DecodeInputStreamFactory<InputStream, Reader, W>, EncodeOutputStreamFactory<OutputStream, Writer, W> {
		@NonNull
		private final Function<? super InputStream, ? extends Reader, ? extends IOException> decoder;

		public CodecResource(@NonNull W source) {
			this(source, OutputStreamWriter::new, InputStreamReader::new);
		}

		public CodecResource(@NonNull W source, @NonNull CharsetEncoder charsetEncoder,
				@NonNull CharsetDecoder charsetDecoder) {
			this(source, (e) -> new OutputStreamWriter(e, charsetEncoder),
					(e) -> new InputStreamReader(e, charsetDecoder));
		}

		public CodecResource(@NonNull W source,
				@NonNull Function<? super OutputStream, ? extends Writer, ? extends IOException> encoder,
				@NonNull Function<? super InputStream, ? extends Reader, ? extends IOException> decoder) {
			super(source, encoder);
			this.decoder = decoder;
		}
	}

	@Data
	public static class RenamedResource<W extends Resource> implements ResourceWrapper<W> {
		@NonNull
		private final String name;
		@NonNull
		private final W source;
	}

	@FunctionalInterface
	public static interface ResourceWrapper<W extends Resource> extends Resource,
			InputStreamFactoryWrapper<InputStream, W>, OutputStreamFactoryWrapper<OutputStream, W>, NamedWrapper<W> {
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
		default @NonNull Pipeline<InputStream, IOException> getInputStream() throws UnsupportedOperationException {
			return getSource().getInputStream();
		}

		@Override
		default @NonNull Pipeline<OutputStream, IOException> getOutputStream() throws UnsupportedOperationException {
			return getSource().getOutputStream();
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
		default boolean isWritable() {
			return getSource().isWritable();
		}

		@Override
		default long lastModified() throws IOException {
			return getSource().lastModified();
		}

		@Override
		default Resource map(@NonNull Charset charset) {
			return getSource().map(charset);
		}

		@Override
		default Resource map(@NonNull CharsetEncoder charsetEncoder, @NonNull CharsetDecoder charsetDecoder) {
			return getSource().map(charsetEncoder, charsetDecoder);
		}

		@Override
		default Resource map(@NonNull Function<? super OutputStream, ? extends Writer, ? extends IOException> encoder,
				@NonNull Function<? super InputStream, ? extends Reader, ? extends IOException> decoder) {
			return getSource().map(encoder, decoder);
		}

		@Override
		default Resource map(@NonNull String charsetName) {
			return getSource().map(charsetName);
		}

		@Override
		default Resource rename(String name) {
			return getSource().rename(name);
		}
	}

	long contentLength() throws IOException;

	Resource createRelative(String relativePath) throws IOException;

	boolean exists();

	String getDescription();

	File getFile() throws IOException, FileNotFoundException;

	@Override
	@NonNull
	Pipeline<InputStream, IOException> getInputStream() throws UnsupportedOperationException;

	@Override
	@NonNull
	default Pipeline<OutputStream, IOException> getOutputStream() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	URI getURI() throws IOException;

	URL getURL() throws IOException;

	default boolean isFile() {
		return false;
	}

	default boolean isReadable() {
		return exists();
	}

	default boolean isWritable() {
		return false;
	}

	default Resource map(@NonNull Charset charset) {
		return new CharsetResource<>(this, charset);
	}

	default Resource map(@NonNull CharsetEncoder charsetEncoder, @NonNull CharsetDecoder charsetDecoder) {
		return new CodecResource<>(this, charsetEncoder, charsetDecoder);
	}

	default Resource map(@NonNull Function<? super OutputStream, ? extends Writer, ? extends IOException> encoder,
			@NonNull Function<? super InputStream, ? extends Reader, ? extends IOException> decoder) {
		return new CodecResource<>(this, encoder, decoder);
	}

	default Resource map(@NonNull String charsetName) {
		return new CharsetResource<>(this, charsetName);
	}

	@Override
	default Resource rename(String name) {
		return new RenamedResource<>(name, this);
	}
}

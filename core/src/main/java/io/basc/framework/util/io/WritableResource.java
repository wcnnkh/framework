package io.basc.framework.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import io.basc.framework.util.Channel;
import io.basc.framework.util.Pipeline;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

public interface WritableResource extends Resource, OutputStreamFactory<OutputStream> {
	public static class CharsetWritableResource<W extends WritableResource> extends CodecWritableResource<W>
			implements CharsetCapable {
		private final Object charset;

		public CharsetWritableResource(@NonNull W source, @NonNull Charset charset) {
			super(source, (os) -> new OutputStreamWriter(os, charset), (is) -> new InputStreamReader(is, charset));
			this.charset = charset;
		}

		public CharsetWritableResource(@NonNull W source, @NonNull String charsetName) {
			super(source, (os) -> new OutputStreamWriter(os, charsetName),
					(is) -> new InputStreamReader(is, charsetName));
			this.charset = charsetName;
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

	@Data
	public static class CodecWritableResource<W extends WritableResource>
			implements WritableResourceWrapper<W>, ReaderFactory<Reader>, WriterFactory<Writer> {
		@NonNull
		private final W source;
		@NonNull
		private final Pipeline<? super OutputStream, ? extends Writer, ? extends IOException> encoder;
		@NonNull
		private final Pipeline<? super InputStream, ? extends Reader, ? extends IOException> decoder;

		@Override
		public @NonNull Channel<Reader, IOException> getReader() {
			return toReaderFactory().getReader();
		}

		@Override
		public @NonNull Channel<Writer, IOException> getWriter() {
			return toWriterFactory().getWriter();
		}

		@Override
		public ReaderFactory<Reader> toReaderFactory() {
			return source.map(decoder);
		}

		@Override
		public WriterFactory<Writer> toWriterFactory() {
			return source.map(encoder);
		}
	}

	@Getter
	public static class EncodeWritableResource<W extends WritableResource>
			extends MappedOutputStreamFactory<OutputStream, Writer, W> implements WritableResourceWrapper<W> {

		public EncodeWritableResource(@NonNull W source,
				@NonNull Pipeline<? super OutputStream, ? extends Writer, ? extends IOException> pipeline) {
			super(source, pipeline);
		}
	}

	@FunctionalInterface
	public static interface WritableResourceWrapper<W extends WritableResource>
			extends WritableResource, ResourceWrapper<W>, OutputStreamFactoryWrapper<OutputStream, W> {
		@Override
		default boolean isWritable() {
			return getSource().isWritable();
		}

		@Override
		default WritableResource codec(
				@NonNull Pipeline<? super OutputStream, ? extends Writer, ? extends IOException> encoder,
				@NonNull Pipeline<? super InputStream, ? extends Reader, ? extends IOException> decoder) {
			return getSource().codec(encoder, decoder);
		}

		@Override
		default WritableResource encode(Charset charset) {
			return getSource().encode(charset);
		}

		@Override
		default WritableResource encode(
				@NonNull Pipeline<? super OutputStream, ? extends Writer, ? extends IOException> pipeline) {
			return getSource().encode(pipeline);
		}

		@Override
		default WritableResource encode(String charsetName) {
			return getSource().encode(charsetName);
		}
	}

	default WritableResource codec(
			@NonNull Pipeline<? super OutputStream, ? extends Writer, ? extends IOException> encoder,
			@NonNull Pipeline<? super InputStream, ? extends Reader, ? extends IOException> decoder) {
		return new CodecWritableResource<>(this, encoder, decoder);
	}

	default WritableResource encode(Charset charset) {
		return new CharsetWritableResource<>(this, charset);
	}

	default WritableResource encode(
			@NonNull Pipeline<? super OutputStream, ? extends Writer, ? extends IOException> pipeline) {
		return new EncodeWritableResource<WritableResource>(this, pipeline);
	}

	default WritableResource encode(String charsetName) {
		return new CharsetWritableResource<>(this, charsetName);
	}

	/**
	 * Indicate whether the contents of this resource can be written via
	 * {@link #getOutputStream()}.
	 * <p>
	 * Will be {@code true} for typical resource descriptors; note that actual
	 * content writing may still fail when attempted. However, a value of
	 * {@code false} is a definitive indication that the resource content cannot be
	 * modified.
	 * 
	 * @see #getOutputStream()
	 * @see #isReadable()
	 */
	default boolean isWritable() {
		return true;
	}
}

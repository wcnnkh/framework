package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

@FunctionalInterface
public interface ResourceWrapper<W extends Resource> extends Resource, InputSourceWrapper<W>, OutputSourceWrapper<W> {
	@Override
	default String getName() {
		return getSource().getName();
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
	default long lastModified() throws IOException {
		return getSource().lastModified();
	}

	@Override
	default long contentLength() throws IOException {
		return getSource().contentLength();
	}

	@Override
	default boolean isOpen() {
		return getSource().isOpen();
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
	default Resource encode(Charset charset) {
		return getSource().encode(charset);
	}

	@Override
	default Resource encode(String charsetName) {
		return getSource().encode(charsetName);
	}

	@Override
	default Resource decode(@NonNull Charset charset) {
		return getSource().decode(charset);
	}

	@Override
	default Resource decode(@NonNull String charsetName) {
		return getSource().decode(charsetName);
	}

	@Override
	default Resource codec(@NonNull Charset charset) {
		return getSource().codec(charset);
	}

	@Override
	default Resource codec(String charsetName) {
		return getSource().codec(charsetName);
	}

	@Override
	default Resource codec(@NonNull ThrowingFunction<? super OutputStream, ? extends Writer, IOException> encoder,
			@NonNull ThrowingFunction<? super InputStream, ? extends Reader, IOException> decoder) {
		return getSource().codec(encoder, decoder);
	}

	@Override
	default Resource rename(String name) {
		return getSource().rename(name);
	}
}
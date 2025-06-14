package run.soeasy.framework.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;

@FunctionalInterface
public interface ResourceWrapper<W extends Resource>
		extends Resource, InputSourceWrapper<InputStream, Reader, W>, OutputSourceWrapper<OutputStream, Writer, W> {
	@Override
	default String getName() {
		return getSource().getName();
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
	default InputStream getInputStream() throws IOException {
		return getSource().getInputStream();
	}

	@Override
	default boolean isDecoded() {
		return getSource().isDecoded();
	}

	@Override
	default Reader getReader() throws IOException {
		return getSource().getReader();
	}

	@Override
	default boolean isWritable() {
		return getSource().isWritable();
	}

	@Override
	default OutputStream getOutputStream() throws IOException {
		return getSource().getOutputStream();
	}

	@Override
	default boolean isEncoded() {
		return getSource().isEncoded();
	}

	@Override
	default Writer getWriter() throws IOException {
		return getSource().getWriter();
	}
}
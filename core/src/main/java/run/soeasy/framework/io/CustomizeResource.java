package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.function.Pipeline;

@Getter
@Setter
public class CustomizeResource implements Resource {
	private long contentLength = -1;
	private String description;
	private InputSource inputSource;
	private long lastModified = 0;
	private String name;
	private OutputSource outputSource;

	@Override
	public long contentLength() throws IOException {
		return contentLength;
	}

	@Override
	public String getDescription() {
		return description == null ? Resource.super.getDescription() : description;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (inputSource == null) {
			throw new UnsupportedOperationException();
		}
		return inputSource.getInputStream();
	}

	@Override
	public @NonNull Pipeline<InputStream, IOException> getInputStreamPipeline() {
		if (inputSource == null) {
			throw new UnsupportedOperationException();
		}
		return inputSource.getInputStreamPipeline();
	}

	@Override
	public String getName() {
		return name == null ? Resource.super.getName() : name;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		if (outputSource == null) {
			throw new UnsupportedOperationException();
		}
		return outputSource.getOutputStream();
	}

	@Override
	public @NonNull Pipeline<OutputStream, IOException> getOutputStreamPipeline() {
		if (outputSource == null) {
			throw new UnsupportedOperationException();
		}
		return outputSource.getOutputStreamPipeline();
	}

	@Override
	public Reader getReader() throws IOException {
		if (inputSource == null) {
			throw new UnsupportedOperationException();
		}
		return inputSource.getReader();
	}

	@Override
	public @NonNull Pipeline<Reader, IOException> getReaderPipeline() {
		if (inputSource == null) {
			throw new UnsupportedOperationException();
		}
		return inputSource.getReaderPipeline();
	}

	@Override
	public Writer getWriter() throws IOException {
		if (outputSource == null) {
			throw new UnsupportedOperationException();
		}
		return outputSource.getWriter();
	}

	@Override
	public @NonNull Pipeline<Writer, IOException> getWriterPipeline() {
		if (outputSource == null) {
			throw new UnsupportedOperationException();
		}
		return outputSource.getWriterPipeline();
	}

	@Override
	public boolean isDecoded() {
		return inputSource != null ? inputSource.isDecoded() : Resource.super.isDecoded();
	}

	@Override
	public boolean isEncoded() {
		return outputSource != null ? outputSource.isEncoded() : Resource.super.isEncoded();
	}

	@Override
	public boolean isReadable() {
		return inputSource != null;
	}

	@Override
	public boolean isWritable() {
		return inputSource != null;
	}

	@Override
	public long lastModified() throws IOException {
		return lastModified;
	}

	@Override
	public final String toString() {
		return getDescription();
	}
}

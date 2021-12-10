package io.basc.framework.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

import io.basc.framework.util.Wrapper;
import io.basc.framework.util.stream.ConsumerProcessor;
import io.basc.framework.util.stream.Processor;

public class InputStreamSourceWrapper<I extends InputStreamSource> extends Wrapper<I> implements InputStreamSource {

	public InputStreamSourceWrapper(I wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return wrappedTarget.getInputStream();
	}

	@Override
	public byte[] getBytes() throws IOException {
		return wrappedTarget.getBytes();
	}

	@Override
	public <E extends Throwable> void read(ConsumerProcessor<InputStream, E> callback) throws IOException, E {
		wrappedTarget.read(callback);
	}

	@Override
	public <T, E extends Throwable> T read(Processor<InputStream, ? extends T, E> processor) throws IOException, E {
		return wrappedTarget.read(processor);
	}

	@Override
	public ReadableByteChannel readableChannel() throws IOException {
		return wrappedTarget.readableChannel();
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		wrappedTarget.transferTo(dest);
	}

	@Override
	public void transferTo(Path dest) throws IOException, IllegalStateException {
		wrappedTarget.transferTo(dest);
	}
}

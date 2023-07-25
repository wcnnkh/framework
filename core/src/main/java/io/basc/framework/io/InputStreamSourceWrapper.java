package io.basc.framework.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

import io.basc.framework.util.Wrapper;
import io.basc.framework.util.function.ConsumeProcessor;
import io.basc.framework.util.function.Processor;

public class InputStreamSourceWrapper<W extends InputStreamSource> extends Wrapper<W> implements InputStreamSource {

	public InputStreamSourceWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return wrappedTarget.getInputStream();
	}

	@Override
	public <E extends Throwable> void consume(ConsumeProcessor<? super InputStream, ? extends E> processor)
			throws IOException, E {
		wrappedTarget.consume(processor);
	}

	@Override
	public byte[] getBytes() throws IOException {
		return wrappedTarget.getBytes();
	}

	@Override
	public <T, E extends Throwable> T read(Processor<? super InputStream, ? extends T, ? extends E> processor)
			throws IOException, E {
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

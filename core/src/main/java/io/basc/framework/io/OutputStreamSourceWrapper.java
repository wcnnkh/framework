package io.basc.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Processor;
import io.basc.framework.util.Wrapper;

public class OutputStreamSourceWrapper<W extends OutputStreamSource> extends Wrapper<W> implements OutputStreamSource {

	public OutputStreamSourceWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return wrappedTarget.getOutputStream();
	}

	@Override
	public <E extends Throwable> void produce(ConsumeProcessor<? super OutputStream, ? extends E> processor)
			throws IOException, E {
		wrappedTarget.produce(processor);
	}

	@Override
	public <T, E extends Throwable> T write(Processor<? super OutputStream, ? extends T, ? extends E> processor)
			throws IOException, E {
		return wrappedTarget.write(processor);
	}

	@Override
	public WritableByteChannel writableChannel() throws IOException {
		return wrappedTarget.writableChannel();
	}
}

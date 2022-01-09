package io.basc.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

import io.basc.framework.util.Wrapper;
import io.basc.framework.util.stream.ConsumerProcessor;
import io.basc.framework.util.stream.Processor;

public class OutputStreamSourceWrapper<W extends OutputStreamSource> extends Wrapper<W> implements OutputStreamSource {

	public OutputStreamSourceWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return wrappedTarget.getOutputStream();
	}

	@Override
	public WritableByteChannel writableChannel() throws IOException {
		return wrappedTarget.writableChannel();
	}

	@Override
	public <E extends Throwable> void produce(ConsumerProcessor<OutputStream, E> callback) throws IOException, E {
		wrappedTarget.produce(callback);
	}

	@Override
	public <T, E extends Throwable> T write(Processor<OutputStream, ? extends T, E> processor) throws IOException, E {
		return wrappedTarget.write(processor);
	}

}

package scw.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

import scw.util.Wrapper;

public class OutputStreamSourceWrapper<W extends OutputStreamSource> extends Wrapper<W>
		implements OutputStreamSource {

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
	public void write(IoCallback<OutputStream> callback) throws IOException {
		wrappedTarget.write(callback);
		;
	}

	@Override
	public <T> T write(IoProcessor<OutputStream, ? extends T> processor) throws IOException {
		return wrappedTarget.write(processor);
	}
}

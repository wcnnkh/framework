package scw.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

import scw.util.Wrapper;

public class InputStreamSourceWrapper<I extends InputStreamSource> extends Wrapper<I>
		implements InputStreamSource {

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
	public void read(IoCallback<InputStream> callback) throws IOException {
		wrappedTarget.read(callback);
	}

	@Override
	public <T> T read(IoProcessor<InputStream, ? extends T> processor) throws IOException {
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

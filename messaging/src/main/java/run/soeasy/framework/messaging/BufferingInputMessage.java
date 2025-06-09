package run.soeasy.framework.messaging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.domain.Wrapped;
import run.soeasy.framework.core.function.Pipeline;

@Setter
public class BufferingInputMessage<W extends InputMessage> extends Wrapped<W> implements InputMessageWrapper<W> {
	private InputStream inputStream;

	public BufferingInputMessage(W source) {
		super(source);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (inputStream == null) {
			byte[] data = getSource().readAllBytes();
			inputStream = new ByteArrayInputStream(data);
		}
		return inputStream;
	}

	@Override
	public @NonNull Pipeline<InputStream, IOException> getInputStreamPipeline() {
		return Pipeline.forSupplier(() -> getInputStream());
	}

	@Override
	public InputMessage buffered() {
		return this;
	}
}
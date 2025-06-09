package run.soeasy.framework.messaging;

import java.io.IOException;
import java.io.OutputStream;

import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.domain.Wrapped;
import run.soeasy.framework.core.function.Pipeline;

@Setter
public class BufferingOutputMessage<W extends OutputMessage> extends Wrapped<W> implements OutputMessageWrapper<W> {
	private OutputStream outputStream;

	public BufferingOutputMessage(W source) {
		super(source);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		if (outputStream == null) {
			outputStream = getSource().getOutputStream();
		}
		return outputStream;
	}

	@Override
	public @NonNull Pipeline<OutputStream, IOException> getOutputStreamPipeline() {
		return Pipeline.forSupplier(() -> getOutputStream());
	}

	@Override
	public OutputMessage buffered() {
		return this;
	}
}
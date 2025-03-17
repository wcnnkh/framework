package run.soeasy.framework.net.convert.support;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.core.convert.TargetDescriptor;
import run.soeasy.framework.net.InetUtils;
import run.soeasy.framework.net.InputMessage;
import run.soeasy.framework.net.MediaType;
import run.soeasy.framework.net.Message;
import run.soeasy.framework.net.OutputMessage;
import run.soeasy.framework.util.io.InputStreamSource;
import run.soeasy.framework.util.io.MimeType;

@RequiredArgsConstructor
@Getter
public abstract class AbstractInputMessageConverter<T extends InputMessage> extends AbstractMessageConverter {
	@NonNull
	private final Class<? extends T> inputMessageClass;

	@Override
	public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) {
		return targetDescriptor.getRequiredTypeDescriptor().getType().isAssignableFrom(inputMessageClass)
				&& super.isReadable(targetDescriptor, message, contentType);
	}

	@Override
	public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
			MimeType contentType) {
		return InputMessage.class.isAssignableFrom(sourceDescriptor.getTypeDescriptor().getType())
				&& !message.getHeaders().isReadyOnly() && super.isWriteable(sourceDescriptor, message, contentType);
	}

	@Override
	protected void doWrite(@NonNull Source source, @NonNull OutputMessage message, @NonNull MediaType contentType)
			throws IOException {
		InputMessage input = source.getAsObject(InputMessage.class);
		InetUtils.writeHeader(input, message);
		input.transferTo(message);
	}

	@Override
	protected Object doRead(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException {
		return readToInputMessage(message, message, targetDescriptor);
	}

	protected abstract T readToInputMessage(@NonNull Message message, InputStreamSource<?> source,
			@NonNull TargetDescriptor targetDescriptor) throws IOException;

}

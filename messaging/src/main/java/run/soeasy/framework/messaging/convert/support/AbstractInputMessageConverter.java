package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.value.Readable;
import run.soeasy.framework.core.convert.value.ValueAccessor;
import run.soeasy.framework.core.convert.value.Writeable;
import run.soeasy.framework.core.io.InputStreamSource;
import run.soeasy.framework.core.io.MimeType;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;

@RequiredArgsConstructor
@Getter
public abstract class AbstractInputMessageConverter<T extends InputMessage> extends AbstractMessageConverter {
	@NonNull
	private final Class<? extends T> inputMessageClass;

	@Override
	public boolean isReadable(@NonNull Writeable targetDescriptor, @NonNull Message message,
			MimeType contentType) {
		return targetDescriptor.getRequiredTypeDescriptor().getType().isAssignableFrom(inputMessageClass)
				&& super.isReadable(targetDescriptor, message, contentType);
	}

	@Override
	public boolean isWriteable(@NonNull Readable sourceDescriptor, @NonNull Message message,
			MimeType contentType) {
		return InputMessage.class.isAssignableFrom(sourceDescriptor.getTypeDescriptor().getType())
				&& !message.getHeaders().isReadyOnly() && super.isWriteable(sourceDescriptor, message, contentType);
	}

	@Override
	protected void doWrite(@NonNull ValueAccessor source, @NonNull OutputMessage message, @NonNull MediaType contentType)
			throws IOException {
		InputMessage input = source.getAsObject(InputMessage.class);
		writeHeader(input, message);
		input.transferTo(message);
	}

	@Override
	protected Object doRead(@NonNull Writeable targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException {
		return readToInputMessage(message, message, targetDescriptor);
	}

	protected abstract T readToInputMessage(@NonNull Message message, InputStreamSource<?> source,
			@NonNull Writeable targetDescriptor) throws IOException;

}

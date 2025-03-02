package io.basc.framework.net.convert.support;

import java.io.IOException;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.InetUtils;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.io.InputStreamSource;
import io.basc.framework.util.io.MimeType;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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

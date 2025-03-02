package io.basc.framework.net.convert.support;

import java.io.IOException;

import io.basc.framework.core.convert.Data;
import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.io.MimeType;
import lombok.NonNull;

public abstract class ObjectMessageConverter<T> extends AbstractMessageConverter {
	private final Class<? extends T> requriedType;

	public ObjectMessageConverter(@NonNull Class<? extends T> requriedType) {
		this.requriedType = requriedType;
	}

	@Override
	protected Object doRead(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException {
		return readObject(targetDescriptor, message, contentType);
	}

	protected abstract T readObject(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException;

	@Override
	protected void doWrite(@NonNull Source source, @NonNull OutputMessage message, @NonNull MediaType contentType)
			throws IOException {
		Data<T> data = source.getAsData(requriedType);
		writeObject(data, message, contentType);
	}

	protected abstract void writeObject(@NonNull Data<T> data, @NonNull OutputMessage message,
			@NonNull MediaType contentType) throws IOException;
}

package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.core.io.MimeType;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.OutputMessage;

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
	protected void doWrite(@NonNull TypedValue source, @NonNull OutputMessage message, @NonNull MediaType contentType)
			throws IOException {
		TypedData<T> data = source.getAsData(requriedType);
		writeObject(data, message, contentType);
	}

	protected abstract void writeObject(@NonNull TypedData<T> data, @NonNull OutputMessage message,
			@NonNull MediaType contentType) throws IOException;
}

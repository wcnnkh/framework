package run.soeasy.framework.net.convert.support;

import java.io.IOException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Data;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TargetDescriptor;
import run.soeasy.framework.net.InputMessage;
import run.soeasy.framework.net.MediaType;
import run.soeasy.framework.net.OutputMessage;
import run.soeasy.framework.util.io.MimeType;

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

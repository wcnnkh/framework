package io.basc.framework.net.convert.support;

import java.io.IOException;

import io.basc.framework.core.convert.Data;
import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.net.Response;
import io.basc.framework.util.io.MimeType;
import lombok.NonNull;

public abstract class ObjectMessageConverter<T> extends AbstractMessageConverter {
	private final Class<? extends T> requriedType;

	public ObjectMessageConverter(@NonNull Class<? extends T> requriedType) {
		this.requriedType = requriedType;
	}

	@Override
	protected Object doRead(@NonNull TargetDescriptor targetDescriptor, MimeType contentType,
			@NonNull InputMessage request, @NonNull Response response) throws IOException {
		return readObject(targetDescriptor, contentType, request, response);
	}

	protected abstract T readObject(@NonNull TargetDescriptor targetDescriptor, MimeType contentType,
			@NonNull InputMessage request, @NonNull Response response) throws IOException;

	@Override
	protected void doWrite(Source source, MediaType contentType, Request request, OutputMessage response)
			throws IOException {
		Data<T> data = source.getAsData(requriedType);
		writeObject(data, contentType, request, response);
	}

	protected abstract void writeObject(Data<T> data, MediaType contentType, Request request, OutputMessage response)
			throws IOException;
}

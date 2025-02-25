package io.basc.framework.net.convert.support;

import java.io.IOException;

import io.basc.framework.core.convert.Data;
import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.util.io.MimeType;
import lombok.NonNull;

public abstract class ObjectMessageConverter<T> extends AbstractMessageConverter {
	private final Class<? extends T> requriedType;

	public ObjectMessageConverter(@NonNull Class<? extends T> requriedType) {
		this.requriedType = requriedType;
	}

	@Override
	protected final Object doRead(TargetDescriptor targetDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException {
		return readFrom(targetDescriptor, contentType, inputMessage);
	}

	protected abstract T readObject(TargetDescriptor targetDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException;

	@Override
	protected final void doWrite(Source source, MediaType contentType, Request request, OutputMessage outputMessage)
			throws IOException {
		Data<T> data = source.getAsData(requriedType);
		writeObject(data, contentType, request, outputMessage);
	}

	protected abstract void writeObject(Data<T> data, MediaType contentType, Request request,
			OutputMessage outputMessage) throws IOException;
}

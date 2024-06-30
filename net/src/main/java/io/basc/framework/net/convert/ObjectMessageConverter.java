package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.Value;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.OutputMessage;

public abstract class ObjectMessageConverter<T> extends AbstractMessageConverter {

	protected long getContentLength(T source, MimeType contentType) {
		return -1;
	}

	@Override
	protected final Object doRead(TypeDescriptor typeDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException {
		return read(typeDescriptor, contentType, inputMessage);
	}

	protected abstract T read(TypeDescriptor typeDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException;

	@SuppressWarnings("unchecked")
	@Override
	protected final void doWrite(Value source, MimeType contentType, OutputMessage outputMessage) throws IOException {
		T value = (T) source.getValue();
		if (outputMessage.getContentLength() < 0) {
			Long contentLength = getContentLength(value, contentType);
			if (contentLength != null && contentLength >= 0) {
				outputMessage.setContentLength(contentLength);
			}
		}
		write(source.getTypeDescriptor(), value, contentType, outputMessage);
	}

	protected abstract void write(TypeDescriptor sourceTypeDescriptor, T source, MimeType contentType,
			OutputMessage outputMessage) throws IOException;
}

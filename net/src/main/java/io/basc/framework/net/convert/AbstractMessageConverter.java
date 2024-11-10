package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ValueWrapper;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.Message;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.OutputMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractMessageConverter implements MessageConverter {
	private final MimeTypes mimeTypes = new MimeTypes();

	protected abstract Object doRead(TypeDescriptor typeDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException;

	protected abstract void doWrite(ValueWrapper source, MimeType contentType, OutputMessage outputMessage) throws IOException;

	protected MimeType getContentType(TypeDescriptor type, Message message) throws IOException {
		MimeType mimeType = mimeTypes.getMimeTypes().first();
		if (mimeType.isWildcardType() || mimeType.isWildcardSubtype()) {
			return null;
		}
		return mimeType;
	}

	@Override
	public MimeTypes getSupportedMediaTypes() {
		return mimeTypes.readyOnly();
	}

	@Override
	public boolean isReadable(TypeDescriptor typeDescriptor, @Nullable MimeType contentType) {
		if (contentType == null) {
			return true;
		}

		for (MimeType mimeType : mimeTypes) {
			if (mimeType.includes(contentType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isWriteable(TypeDescriptor typeDescriptor, @Nullable MimeType contentType) {
		if (contentType == null || MimeTypeUtils.ALL.equalsTypeAndSubtype(contentType)) {
			return true;
		}

		for (MimeType mimeType : mimeTypes) {
			if (mimeType.isCompatibleWith(contentType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public final Object readFrom(TypeDescriptor typeDescriptor, InputMessage inputMessage) throws IOException {
		MimeType contentTypeToUse = inputMessage.getContentType();
		if (contentTypeToUse == null) {
			contentTypeToUse = mimeTypes.getMimeTypes().first();
		}

		return doRead(typeDescriptor, contentTypeToUse, inputMessage);
	}

	@Override
	public final void writeTo(ValueWrapper source, @Nullable MimeType contentType, OutputMessage outputMessage)
			throws IOException {
		MimeType contentTypeToUse = contentType;
		if (contentType == null) {
			contentTypeToUse = outputMessage.getContentType();
		} else if (outputMessage.getContentType() == null) {
			if (contentTypeToUse == null || contentTypeToUse.isWildcardType() || contentTypeToUse.isWildcardSubtype()) {
				contentTypeToUse = getContentType(source.getTypeDescriptor(), outputMessage);
			}
			outputMessage.setContentType(contentTypeToUse);
		}
		doWrite(source, contentTypeToUse, outputMessage);
	}
}

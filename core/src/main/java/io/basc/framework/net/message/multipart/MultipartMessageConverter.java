package io.basc.framework.net.message.multipart;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.DefaultHttpInputMessage;
import io.basc.framework.http.HttpInputMessage;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.convert.MessageConvertException;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class MultipartMessageConverter extends MultipartMessageWriter {

	@Nullable
	private final MultipartMessageResolver multipartMessageResolver;

	public MultipartMessageConverter(@Nullable MultipartMessageResolver multipartMessageResolver) {
		this.multipartMessageResolver = multipartMessageResolver;
		supportMimeTypes.add(MimeTypeUtils.MULTIPART_FORM_DATA);
	}

	public final MultipartMessageResolver getMultipartMessageResolver() {
		return multipartMessageResolver;
	}

	protected boolean canReadType(TypeDescriptor type) {
		if (type.isArray() || type.isCollection()) {
			return type.getElementTypeDescriptor().getType() == MultipartMessage.class;
		}

		return type.getType() == MultipartMessage.class;
	}

	@Override
	public boolean canRead(TypeDescriptor type) {
		if (multipartMessageResolver == null) {
			return false;
		}

		return canReadType(type);
	}

	protected Object convert(Collection<MultipartMessage> messages, TypeDescriptor typeDescriptor) {
		if (CollectionUtils.isEmpty(messages)) {
			return null;
		}

		if (typeDescriptor.getType() == messages.getClass()) {
			return messages;
		}

		if (typeDescriptor.isArray()) {
			return messages.toArray(new MultipartMessage[0]);
		} else if (typeDescriptor.isCollection()) {
			Collection<MultipartMessage> collections = CollectionFactory.createCollection(typeDescriptor.getType(),
					typeDescriptor.getElementTypeDescriptor().getType(), messages.size());
			collections.addAll(messages);
			return collections;
		} else {
			return CollectionUtils.first(messages);
		}
	}

	@Override
	protected Object readInternal(TypeDescriptor type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		List<MultipartMessage> fileItems = multipartMessageResolver
				.resolve(inputMessage instanceof HttpInputMessage ? (HttpInputMessage) inputMessage
						: new DefaultHttpInputMessage(inputMessage));
		return convert(fileItems, type);
	}
}

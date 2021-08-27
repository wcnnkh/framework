package io.basc.framework.net.message.multipart;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.DefaultHttpInputMessage;
import io.basc.framework.http.HttpInputMessage;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.convert.MessageConvertException;

import java.io.IOException;
import java.util.List;

public class MultipartMessageConverter extends MultipartMessageWriter {

	private final MultipartMessageResolver multipartMessageResolver;

	public MultipartMessageConverter(MultipartMessageResolver multipartMessageResolver) {
		this.multipartMessageResolver = multipartMessageResolver;
		supportMimeTypes.add(MimeTypeUtils.MULTIPART_FORM_DATA);
	}

	public final MultipartMessageResolver getMultipartMessageResolver() {
		return multipartMessageResolver;
	}

	@Override
	public boolean canRead(TypeDescriptor type) {
		if (multipartMessageResolver == null) {
			return false;
		}

		if (Iterable.class.isAssignableFrom(type.getType())) {
			return type.getResolvableType().getGeneric(0).getRawClass() == MultipartMessage.class;
		} else if (type.isArray()) {
			return type.getResolvableType().getComponentType().getRawClass() == MultipartMessage.class;
		}
		return false;
	}

	@Override
	protected Object readInternal(TypeDescriptor type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		List<MultipartMessage> fileItems = multipartMessageResolver
				.resolve(inputMessage instanceof HttpInputMessage ? (HttpInputMessage) inputMessage
						: new DefaultHttpInputMessage(inputMessage));
		if (type.isArray()) {
			return fileItems.toArray(new MultipartMessage[0]);
		}
		return fileItems;
	}
}

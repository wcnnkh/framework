package scw.net.message.multipart;

import java.io.IOException;
import java.util.List;

import scw.convert.TypeDescriptor;
import scw.http.DefaultHttpInputMessage;
import scw.http.HttpInputMessage;
import scw.net.MimeTypeUtils;
import scw.net.message.InputMessage;
import scw.net.message.convert.MessageConvertException;

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

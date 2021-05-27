package scw.net.message.convert;

import java.io.IOException;
import java.util.Comparator;
import java.util.TreeSet;

import scw.convert.TypeDescriptor;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.MimeType;
import scw.net.MimeTypes;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public class MessageConverters implements MessageConverter {
	private static Logger logger = LoggerFactory.getLogger(MessageConverters.class);
	private final TreeSet<MessageConverter> messageConverters = new TreeSet<MessageConverter>(
			new ComparatorMessageConverter());
	private MessageConverter parentMessageConverter;

	public MessageConverters() {
	}

	public MessageConverters(MessageConverter parentMessageConverter) {
		this.parentMessageConverter = parentMessageConverter;
	}

	protected void aware(MessageConverter messageConverter) {
	}

	public void addMessageConverter(MessageConverter messageConverter) {
		synchronized (messageConverter) {
			aware(messageConverter);
			messageConverters.add(messageConverter);
		}
	}

	private static class ComparatorMessageConverter implements Comparator<MessageConverter> {

		public int compare(MessageConverter o1, MessageConverter o2) {
			for (MimeType mimeType1 : o1.getSupportMimeTypes()) {
				for (MimeType mimeType2 : o2.getSupportMimeTypes()) {
					if (mimeType1.equals(mimeType2) || mimeType2.includes(mimeType1)) {
						return -1;
					}
				}
			}
			return 1;
		}
	}

	public Object read(TypeDescriptor type, InputMessage inputMessage) throws IOException, MessageConvertException {
		for (MessageConverter converter : messageConverters) {
			if (converter.canRead(type, inputMessage.getContentType())) {
				if (logger.isTraceEnabled()) {
					logger.trace("{} read type={}, contentType={}", converter, type, inputMessage.getContentType());
				}
				return converter.read(type, inputMessage);
			}
		}

		if (parentMessageConverter != null && parentMessageConverter.canRead(type, inputMessage.getContentType())) {
			return parentMessageConverter.read(type, inputMessage);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("not support read type={}, contentType={}", type, inputMessage.getContentType());
		}
		return null;
	}

	public void write(TypeDescriptor type, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		for (MessageConverter converter : messageConverters) {
			if (converter.canWrite(type, body, contentType)) {
				if (logger.isTraceEnabled()) {
					logger.trace("{} write body={}, contentType={}", converter, body, contentType);
				}
				converter.write(type, body, contentType, outputMessage);
				return;
			}
		}

		if (parentMessageConverter != null && parentMessageConverter.canWrite(type, body, contentType)) {
			parentMessageConverter.write(type, body, contentType, outputMessage);
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("not support wirte body={}, contentType={}", body, contentType);
		}
	}

	public boolean canRead(TypeDescriptor type, MimeType mimeType) {
		for (MessageConverter converter : messageConverters) {
			if (converter.canRead(type, mimeType)) {
				return true;
			}
		}
		return (parentMessageConverter != null && parentMessageConverter.canRead(type, mimeType));
	}

	public boolean canWrite(TypeDescriptor type, Object body, MimeType contentType) {
		for (MessageConverter converter : messageConverters) {
			if (converter.canWrite(type, body, contentType)) {
				return true;
			}
		}
		return (parentMessageConverter != null && parentMessageConverter.canWrite(type, body, contentType));
	}

	public MimeTypes getSupportMimeTypes() {
		MimeTypes mimeTypes = new MimeTypes();
		for (MessageConverter converter : messageConverters) {
			mimeTypes.getMimeTypes().addAll(converter.getSupportMimeTypes().getMimeTypes());
		}

		if (parentMessageConverter != null) {
			mimeTypes.getMimeTypes().addAll(parentMessageConverter.getSupportMimeTypes().getMimeTypes());
		}

		return mimeTypes.readyOnly();
	}
}

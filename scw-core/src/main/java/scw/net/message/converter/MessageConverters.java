package scw.net.message.converter;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import scw.core.ResolvableType;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.MimeType;
import scw.net.MimeTypes;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public class MessageConverters implements
		MessageConverter {
	private static Logger logger = LoggerUtils
			.getLogger(MessageConverters.class);
	private final TreeSet<MessageConverter> messageConverters = new TreeSet<MessageConverter>(new ComparatorMessageConverter());

	public SortedSet<MessageConverter> getMessageConverters() {
		return Collections.synchronizedSortedSet(messageConverters);
	}

	private static class ComparatorMessageConverter implements
			Comparator<MessageConverter> {

		public int compare(MessageConverter o1, MessageConverter o2) {
			for (MimeType mimeType1 : o1.getSupportMimeTypes()) {
				for (MimeType mimeType2 : o2.getSupportMimeTypes()) {
					if (mimeType1.equals(mimeType2)
							|| mimeType2.includes(mimeType1)) {
						return -1;
					}
				}
			}
			return 1;
		}
	}

	public Object read(ResolvableType type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		for (MessageConverter converter : messageConverters) {
			if (converter.canRead(type, inputMessage.getContentType())) {
				if (logger.isTraceEnabled()) {
					logger.trace("{} read type={}, contentType={}", converter,
							type, inputMessage.getContentType());
				}
				return converter.read(type, inputMessage);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("not support read type={}, contentType={}", type,
					inputMessage.getContentType());
		}
		return null;
	}

	public void write(ResolvableType type, Object body, MimeType contentType,
			OutputMessage outputMessage) throws IOException,
			MessageConvertException {
		for (MessageConverter converter : messageConverters) {
			if (converter.canWrite(type, body, contentType)) {
				if (logger.isTraceEnabled()) {
					logger.trace("{} write body={}, contentType={}", converter,
							body, contentType);
				}
				converter.write(type, body, contentType, outputMessage);
				return;
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("not support wirte body={}, contentType={}", body,
					contentType);
		}
	}

	public boolean canRead(ResolvableType type, MimeType mimeType) {
		for (MessageConverter converter : messageConverters) {
			if (converter.canRead(type, mimeType)) {
				return true;
			}
		}
		return false;
	}

	public boolean canWrite(ResolvableType type, Object body, MimeType contentType) {
		for (MessageConverter converter : messageConverters) {
			if (converter.canWrite(type, body, contentType)) {
				return true;
			}
		}
		return false;
	}

	public MimeTypes getSupportMimeTypes() {
		MimeTypes mimeTypes = new MimeTypes();
		for (MessageConverter converter : messageConverters) {
			mimeTypes.getMimeTypes().addAll(
					converter.getSupportMimeTypes().getMimeTypes());
		}
		return mimeTypes.readyOnly();
	}

	public boolean canWrite(Object body, MimeType contentType) {
		for (MessageConverter converter : messageConverters) {
			if (converter.canWrite(body, contentType)) {
				return true;
			}
		}
		return false;
	}

	public void write(Object body, MimeType contentType,
			OutputMessage outputMessage) throws IOException,
			MessageConvertException {
		for (MessageConverter converter : messageConverters) {
			if (converter.canWrite(body, contentType)) {
				if (logger.isTraceEnabled()) {
					logger.trace("{} write body={}, contentType={}", converter,
							body, contentType);
				}
				converter.write(body, contentType, outputMessage);
				return;
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("not support wirte body={}, contentType={}", body,
					contentType);
		}
	}
}

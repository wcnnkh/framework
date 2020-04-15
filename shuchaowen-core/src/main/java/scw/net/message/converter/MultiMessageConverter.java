package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.TreeSet;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.MimeType;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.util.MultiEnumeration;

/**
 * add默认为addFirst行为
 * @author shuchaowen
 *
 */
public class MultiMessageConverter extends TreeSet<MessageConverter> implements
		MessageConverter {
	private static Logger logger = LoggerUtils
			.getLogger(MultiMessageConverter.class);
	private static final long serialVersionUID = 1L;

	public MultiMessageConverter() {
		super(new ComparatorMessageConverter());
	}

	private static class ComparatorMessageConverter implements
			Comparator<MessageConverter> {

		public int compare(MessageConverter o1, MessageConverter o2) {
			Enumeration<MimeType> supportMimeTypes1 = o1
					.enumerationSupportMimeTypes();
			while (supportMimeTypes1.hasMoreElements()) {
				MimeType mimeType1 = supportMimeTypes1.nextElement();
				Enumeration<MimeType> supportMimeTypes2 = o2
						.enumerationSupportMimeTypes();
				while (supportMimeTypes2.hasMoreElements()) {
					MimeType mimeType2 = supportMimeTypes2.nextElement();
					if (mimeType1.equals(mimeType2) ||  mimeType2.includes(mimeType1)) {
						return -1;
					}
				}
			}
			return 1;
		}
	}

	public Object read(Type type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		for (MessageConverter converter : this) {
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

	public void write(Object body, MimeType contentType,
			OutputMessage outputMessage) throws IOException,
			MessageConvertException {
		for (MessageConverter converter : this) {
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

	public boolean canRead(Type type, MimeType mimeType) {
		for (MessageConverter converter : this) {
			if (converter.canRead(type, mimeType)) {
				return true;
			}
		}
		return false;
	}

	public boolean canWrite(Object body, MimeType contentType) {
		for (MessageConverter converter : this) {
			if (converter.canWrite(body, contentType)) {
				return true;
			}
		}
		return false;
	}

	public Enumeration<MimeType> enumerationSupportMimeTypes() {
		@SuppressWarnings("unchecked")
		Enumeration<MimeType>[] enumerations = new Enumeration[size()];
		int i = 0;
		for (MessageConverter converter : this) {
			enumerations[i++] = converter.enumerationSupportMimeTypes();
		}
		return new MultiEnumeration<MimeType>(enumerations);
	}
}

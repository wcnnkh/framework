package io.basc.framework.net.convert;

import java.io.IOException;
import java.util.Comparator;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.Value;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.Registration;
import io.basc.framework.util.check.NestingChecker;
import io.basc.framework.util.check.ThreadLocalNestingChecker;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public class ConfigurableMessageConverter extends ConfigurableServices<MessageConverter> implements MessageConverter {
	private static class ComparatorMessageConverter implements Comparator<MessageConverter> {

		public int compare(MessageConverter o1, MessageConverter o2) {
			for (MimeType mimeType1 : o1.getSupportedMediaTypes()) {
				for (MimeType mimeType2 : o2.getSupportedMediaTypes()) {
					if (mimeType1.equals(mimeType2) || mimeType2.includes(mimeType1)) {
						return -1;
					}
				}
			}
			return 1;
		}
	}

	private static final ComparatorMessageConverter COMPARATOR_MESSAGE_CONVERTER = new ComparatorMessageConverter();
	private static Logger logger = LoggerFactory.getLogger(ConfigurableMessageConverter.class);

	private static final NestingChecker<MessageConverter> NESTING_CHECKERS = new ThreadLocalNestingChecker<>();

	@NonNull
	private MessageConverter messageConverterAware = this;

	public ConfigurableMessageConverter() {
		setServiceClass(MessageConverter.class);
		setServiceComparator(COMPARATOR_MESSAGE_CONVERTER);
		getServiceInjectors().register((e) -> {
			if (e instanceof MessageConverterAware) {
				((MessageConverterAware) e).setMessageConverter(messageConverterAware);
			}
			return Registration.EMPTY;
		});
	}

	@Override
	public MimeTypes getSupportedMediaTypes() {
		MimeTypes mimeTypes = new MimeTypes();
		for (MessageConverter converter : getServices()) {
			mimeTypes.getMimeTypes().addAll(converter.getSupportedMediaTypes().getMimeTypes());
		}
		return mimeTypes.readyOnly();
	}

	@Override
	public MimeTypes getSupportedMediaTypes(TypeDescriptor typeDescriptor) {
		MimeTypes mimeTypes = new MimeTypes();
		for (MessageConverter converter : getServices()) {
			mimeTypes.getMimeTypes().addAll(converter.getSupportedMediaTypes(typeDescriptor).getMimeTypes());
		}
		return mimeTypes.readyOnly();
	}

	@Override
	public boolean isReadable(TypeDescriptor typeDescriptor, MimeType contentType) {
		for (MessageConverter converter : getServices()) {
			if (NESTING_CHECKERS.isNestingExists(converter)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(converter);
			try {
				if (converter.isReadable(typeDescriptor, contentType)) {
					return true;
				}
			} finally {
				registration.unregister();
			}
		}
		return false;
	}

	@Override
	public boolean isWriteable(TypeDescriptor typeDescriptor, MimeType contentType) {
		for (MessageConverter converter : getServices()) {
			if (NESTING_CHECKERS.isNestingExists(converter)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(converter);
			try {
				if (converter.isWriteable(typeDescriptor, contentType)) {
					return true;
				}
			} finally {
				registration.unregister();
			}
		}
		return false;
	}

	@Override
	public Object readFrom(TypeDescriptor typeDescriptor, InputMessage inputMessage) throws IOException {
		for (MessageConverter converter : getServices()) {
			if (NESTING_CHECKERS.isNestingExists(converter)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(converter);
			try {
				if (converter.isReadable(typeDescriptor, inputMessage.getContentType())) {
					if (logger.isTraceEnabled()) {
						logger.trace("{} read type={}, contentType={}", converter, typeDescriptor,
								inputMessage.getContentType());
					}
					return converter.readFrom(typeDescriptor, inputMessage);
				}
			} finally {
				registration.unregister();
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("not support read type={}, contentType={}", typeDescriptor, inputMessage.getContentType());
		}
		return null;
	}

	@Override
	public void writeTo(Value value, MimeType contentType, OutputMessage outputMessage) throws IOException {
		for (MessageConverter converter : getServices()) {
			if (NESTING_CHECKERS.isNestingExists(converter)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(converter);
			try {
				if (converter.isWriteable(value.getTypeDescriptor(), contentType)) {
					if (logger.isTraceEnabled()) {
						logger.trace("{} write body={}, contentType={}", converter, value,
								outputMessage.getContentType());
					}
					converter.writeTo(value, contentType, outputMessage);
					return;
				}
			} finally {
				registration.unregister();
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("not support wirte body={}, contentType={}", value, outputMessage.getContentType());
		}
	}

}

package io.basc.framework.net.convert;

import java.io.IOException;
import java.util.Comparator;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.check.NestingChecker;
import io.basc.framework.util.check.ThreadLocalNestingChecker;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.spi.ConfigurableServices;
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
	private static Logger logger = LogManager.getLogger(ConfigurableMessageConverter.class);

	private static final NestingChecker<MessageConverter> NESTING_CHECKERS = new ThreadLocalNestingChecker<>();

	@NonNull
	private MessageConverter messageConverterAware = this;

	public ConfigurableMessageConverter() {
		setServiceClass(MessageConverter.class);
		setComparator(COMPARATOR_MESSAGE_CONVERTER);
		getInjectors().register((e) -> {
			if (e instanceof MessageConverterAware) {
				((MessageConverterAware) e).setMessageConverter(messageConverterAware);
			}
			return Registration.SUCCESS;
		});
	}

	@Override
	public MimeTypes getSupportedMediaTypes() {
		return MimeTypes.forElements(flatMap((e) -> e.getSupportedMediaTypes()));
	}

	@Override
	public MimeTypes getSupportedMediaTypes(TypeDescriptor typeDescriptor) {
		return MimeTypes.forElements(flatMap((e) -> e.getSupportedMediaTypes(typeDescriptor)));
	}

	@Override
	public boolean isReadable(TypeDescriptor typeDescriptor, MimeType contentType) {
		for (MessageConverter converter : this) {
			if (NESTING_CHECKERS.isNestingExists(converter)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(converter);
			try {
				if (converter.isReadable(typeDescriptor, contentType)) {
					return true;
				}
			} finally {
				registration.cancel();
			}
		}
		return false;
	}

	@Override
	public boolean isWriteable(TypeDescriptor typeDescriptor, MimeType contentType) {
		for (MessageConverter converter : this) {
			if (NESTING_CHECKERS.isNestingExists(converter)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(converter);
			try {
				if (converter.isWriteable(typeDescriptor, contentType)) {
					return true;
				}
			} finally {
				registration.cancel();
			}
		}
		return false;
	}

	@Override
	public Object readFrom(TypeDescriptor typeDescriptor, InputMessage inputMessage) throws IOException {
		for (MessageConverter converter : this) {
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
				registration.cancel();
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("not support read type={}, contentType={}", typeDescriptor, inputMessage.getContentType());
		}
		return null;
	}

	@Override
	public void writeTo(Value value, MimeType contentType, OutputMessage outputMessage) throws IOException {
		for (MessageConverter converter : this) {
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
				registration.cancel();
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("not support wirte body={}, contentType={}", value, outputMessage.getContentType());
		}
	}

}

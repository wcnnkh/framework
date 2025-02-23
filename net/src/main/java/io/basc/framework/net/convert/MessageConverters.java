package io.basc.framework.net.convert;

import java.io.IOException;
import java.util.Comparator;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.core.convert.transform.stereotype.AccessDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaTypes;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.net.convert.support.ConfigurableMessageConverter;
import io.basc.framework.util.check.NestingChecker;
import io.basc.framework.util.check.ThreadLocalNestingChecker;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.io.MimeType;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.spi.Providers;
import lombok.NonNull;

public class MessageConverters<T extends MessageConverter> extends Providers<T, ConversionException>
		implements MessageConverter {
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

	public MessageConverters() {
		setNestingChecker(NESTING_CHECKERS);
		setComparator(COMPARATOR_MESSAGE_CONVERTER);
		getInjectors().register((e) -> {
			if (e instanceof MessageConverterAware) {
				((MessageConverterAware) e).setMessageConverter(messageConverterAware);
			}
			return Registration.SUCCESS;
		});
	}

	@Override
	public MediaTypes getSupportedMediaTypes() {
		return MediaTypes.forElements(flatMap((e) -> e.getSupportedMediaTypes()));
	}

	@Override
	public MediaTypes getSupportedMediaTypes(AccessDescriptor requiredDescriptor) {
		return MediaTypes.forElements(flatMap((e) -> e.getSupportedMediaTypes(requiredDescriptor)));
	}

	@Override
	public boolean isReadable(@NonNull AccessDescriptor targetDescriptor, MimeType contentType) {
		return optional().filter((e) -> e.isReadable(targetDescriptor, contentType)).isPresent();
	}

	@Override
	public Object readFrom(@NonNull AccessDescriptor targetDescriptor, @NonNull InputMessage inputMessage)
			throws IOException {
		return optional().filter((e) -> e.isReadable(targetDescriptor, inputMessage.getContentType()))
				.apply((converter) -> {
					if (converter == null) {
						if (logger.isDebugEnabled()) {
							logger.debug("not support read descriptor={}, contentType={}", targetDescriptor,
									inputMessage.getContentType());
						}
						return null;
					}

					if (logger.isTraceEnabled()) {
						logger.trace("{} read descriptor={}, contentType={}", converter, targetDescriptor,
								inputMessage.getContentType());
					}
					return converter.readFrom(targetDescriptor, inputMessage);
				});
	}

	@Override
	public boolean isWriteable(SourceDescriptor sourceDescriptor, MimeType contentType) {
		return optional().filter((e) -> e.isWriteable(sourceDescriptor, contentType)).isPresent();
	}

	@Override
	public void writeTo(Source value, @NonNull Request request, @NonNull OutputMessage outputMessage)
			throws IOException {
		optional().filter((e) -> e.isWriteable(value, outputMessage.getContentType())).map((e) -> {
			if (e == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("not support wirte body={}, contentType={}", value, outputMessage.getContentType());
				}
			}
			return e;
		}).ifPresent((converter) -> {
			if (logger.isTraceEnabled()) {
				logger.trace("{} write body={}, contentType={}", converter, value, outputMessage.getContentType());
			}
			converter.writeTo(value, request, outputMessage);
		});
	}
}

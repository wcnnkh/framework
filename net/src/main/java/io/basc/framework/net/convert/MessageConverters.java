package io.basc.framework.net.convert;

import java.io.IOException;
import java.util.Comparator;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.core.convert.transform.stereotype.AccessDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.MediaTypes;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.check.NestingChecker;
import io.basc.framework.util.check.ThreadLocalNestingChecker;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.io.MimeType;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.spi.Providers;
import lombok.NonNull;

public class MessageConverters extends Providers<MessageConverter, ConversionException> implements MessageConverter {
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
	private static Logger logger = LogManager.getLogger(MessageConverters.class);
	private static final NestingChecker<MessageConverter> NESTING_CHECKERS = new ThreadLocalNestingChecker<>();

	@NonNull
	private MessageConverter messageConverterAware = this;

	public MessageConverters() {
		setServiceClass(MessageConverter.class);
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
	public MediaTypes getSupportedMediaTypes(@NonNull AccessDescriptor requiredDescriptor, @NonNull Message message) {
		return MediaTypes.forElements(flatMap((e) -> e.getSupportedMediaTypes(requiredDescriptor, message)));
	}

	@Override
	public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) {
		return optional().filter((e) -> e.isReadable(targetDescriptor, message, contentType)).isPresent();
	}

	@Override
	public Object readFrom(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException {
		return optional().filter((e) -> e.isReadable(targetDescriptor, message, contentType)).apply((converter) -> {
			if (converter == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("not support read descriptor={}, contentType={}", targetDescriptor, contentType);
				}
				return null;
			}

			if (logger.isTraceEnabled()) {
				logger.trace("{} read descriptor={}, contentType={}", converter, targetDescriptor, contentType);
			}
			return converter.readFrom(targetDescriptor, message, contentType);
		});
	}

	@Override
	public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
			MimeType contentType) {
		return optional().filter((e) -> e.isWriteable(sourceDescriptor, message, contentType)).isPresent();
	}

	@Override
	public void writeTo(@NonNull Source source, @NonNull OutputMessage message, MediaType contentType)
			throws IOException {
		optional().filter((e) -> e.isWriteable(source, message, contentType)).map((e) -> {
			if (e == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("not support wirte body={}, contentType={}", source, contentType);
				}
			}
			return e;
		}).ifPresent((converter) -> {
			if (logger.isTraceEnabled()) {
				logger.trace("{} write body={}, contentType={}", converter, source, contentType);
			}
			converter.writeTo(source, message, contentType);
		});
	}
}

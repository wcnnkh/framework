package run.soeasy.framework.messaging.convert;

import java.io.IOException;
import java.util.Comparator;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.ValueAccessor;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.io.MimeType;
import run.soeasy.framework.core.spi.ServiceProvider;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.MediaTypes;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;

public class MessageConverters extends ServiceProvider<MessageConverter, ConversionException> implements MessageConverter {
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

	@NonNull
	private MessageConverter messageConverterAware = this;

	public MessageConverters() {
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
	public MediaTypes getSupportedMediaTypes() {
		return MediaTypes.forElements(flatMap((e) -> e.getSupportedMediaTypes()));
	}

	@Override
	public MediaTypes getSupportedMediaTypes(@NonNull AccessibleDescriptor requiredDescriptor, @NonNull Message message) {
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
	public void writeTo(@NonNull ValueAccessor source, @NonNull OutputMessage message, MediaType contentType)
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

package io.basc.framework.http.jaxrs;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.convert.MessageConvertException;
import io.basc.framework.net.convert.support.AbstractMessageConverter;
import io.basc.framework.util.exchange.Receipt;
import io.basc.framework.util.io.MimeType;
import io.basc.framework.util.spi.Configurable;
import io.basc.framework.util.spi.ServiceLoaderDiscovery;
import lombok.NonNull;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JaxrsMessageConverter extends AbstractMessageConverter implements Configurable {
	private MessageBodyReaders messageBodyReaders = new MessageBodyReaders<>();
	private MessageBodyWriters messageBodyWriters = new MessageBodyWriters<>();

	{
		messageBodyReaders.setServiceClass(MessageBodyReader.class);
		messageBodyWriters.setServiceClass(MessageBodyWriter.class);
	}

	@Override
	public Receipt doConfigure(@NonNull ServiceLoaderDiscovery discovery) {
		messageBodyReaders.doConfigure(discovery);
		messageBodyWriters.doConfigure(discovery);
		return Receipt.SUCCESS;
	}

	@Override
	public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) {
		if (contentType == null) {
			return false;
		}
		MediaType mediaType = JaxrsUtils.convertMediaType(contentType);
		TypeDescriptor typeDescriptor = targetDescriptor.getRequiredTypeDescriptor();
		return messageBodyReaders.isReadable(typeDescriptor.getType(), typeDescriptor.getResolvableType().getType(),
				typeDescriptor.getAnnotations(), mediaType);
	}

	@Override
	public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
			MimeType contentType) {
		if (contentType == null) {
			return false;
		}
		MediaType mediaType = JaxrsUtils.convertMediaType(contentType);
		TypeDescriptor typeDescriptor = sourceDescriptor.getTypeDescriptor();
		return messageBodyWriters.isWriteable(typeDescriptor.getType(), typeDescriptor.getResolvableType().getType(),
				typeDescriptor.getAnnotations(), mediaType);
	}

	@Override
	protected void doWrite(@NonNull Source source, @NonNull OutputMessage message,
			io.basc.framework.net.@NonNull MediaType contentType) throws IOException {
		MediaType mediaType = JaxrsUtils.convertMediaType(contentType);
		TypeDescriptor typeDescriptor = source.getTypeDescriptor();
		Annotation[] annotations = typeDescriptor.getAnnotations();
		if (messageBodyWriters.isWriteable(typeDescriptor.getType(), typeDescriptor.getResolvableType().getType(),
				annotations, mediaType)) {
			MultivaluedMap<String, String> headerMap = JaxrsUtils.convertHeaders(message.getHeaders());
			message.getOutputStreamPipeline().optional().ifPresent((os) -> {
				messageBodyWriters.writeTo(source.get(), typeDescriptor.getType(),
						typeDescriptor.getResolvableType().getType(), annotations, mediaType, headerMap, os);
			});
		}
	}

	@Override
	protected Object doRead(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException {
		MediaType mediaType = JaxrsUtils.convertMediaType(contentType);
		TypeDescriptor typeDescriptor = targetDescriptor.getRequiredTypeDescriptor();
		Annotation[] annotations = typeDescriptor.getAnnotations();
		if (messageBodyReaders.isReadable(typeDescriptor.getType(), typeDescriptor.getResolvableType().getType(),
				annotations, mediaType)) {
			MultivaluedMap<String, String> headerMap = JaxrsUtils.convertHeaders(message.getHeaders());
			return message.getInputStreamPipeline().optional().apply((is) -> {
				return messageBodyReaders.readFrom(typeDescriptor.getType(),
						typeDescriptor.getResolvableType().getType(), annotations, mediaType, headerMap,
						message.getInputStream());
			});
		}
		throw new MessageConvertException("Unable to read body " + targetDescriptor);
	}
}

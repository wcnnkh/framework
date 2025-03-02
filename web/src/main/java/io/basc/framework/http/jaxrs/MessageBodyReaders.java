package io.basc.framework.http.jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import io.basc.framework.util.spi.ConfigurableServices;

public class MessageBodyReaders<T> extends ConfigurableServices<MessageBodyReader<T>> implements MessageBodyReader<T> {

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return anyMatch((e) -> e.isReadable(type, genericType, annotations, mediaType));
	}

	@Override
	public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		for (MessageBodyReader<T> reader : this) {
			if (reader.isReadable(type, genericType, annotations, mediaType)) {
				return reader.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
			}
		}
		return null;
	}

}

package io.basc.framework.http.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import io.basc.framework.util.spi.ConfigurableServices;

public class MessageBodyWriters<T> extends ConfigurableServices<MessageBodyWriter<T>> implements MessageBodyWriter<T> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return anyMatch((e) -> e.isWriteable(type, genericType, annotations, mediaType));
	}

	@Override
	public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		for (MessageBodyWriter<T> writer : this) {
			if (writer.isWriteable(type, genericType, annotations, mediaType)) {
				writer.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
			}
		}
	}

}

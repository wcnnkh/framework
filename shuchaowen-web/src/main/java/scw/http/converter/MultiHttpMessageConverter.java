package scw.http.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import scw.http.HttpInputMessage;
import scw.http.HttpOutputMessage;
import scw.http.MediaType;
import scw.http.client.RestClientException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class MultiHttpMessageConverter<T> extends
		LinkedList<HttpMessageConverter<T>> implements HttpMessageConverter<T> {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtils
			.getLogger(MultiHttpMessageConverter.class);

	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		for (HttpMessageConverter<T> converter : this) {
			if (converter.canRead(clazz, mediaType)) {
				return true;
			}
		}
		return false;
	}

	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		for (HttpMessageConverter<T> converter : this) {
			if (converter.canWrite(clazz, mediaType)) {
				return true;
			}
		}
		return false;
	}

	public List<MediaType> getSupportedMediaTypes() {
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		for (HttpMessageConverter<T> converter : this) {
			mediaTypes.addAll(converter.getSupportedMediaTypes());
		}
		return mediaTypes;
	}

	public T read(Class<? extends T> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		MediaType contentType = getContentType(inputMessage);
		for (HttpMessageConverter<T> converter : this) {
			if (canRead(clazz, contentType)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Reading [" + clazz + "] as \"" + contentType
							+ "\" using [" + converter + "]");
				}
				return converter.read(clazz, inputMessage);
			}
		}
		throw new RestClientException(
				"Could not extract response: no suitable HttpMessageConverter found "
						+ "for response type [" + clazz
						+ "] and content type [" + contentType + "]");
	}

	public void write(T t, MediaType contentType,
			HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {
		if (t == null) {
			return;
		}

		for (HttpMessageConverter<T> converter : this) {
			if (canWrite(t.getClass(), contentType)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Write [" + t + "] as \"" + contentType
							+ "\" using [" + converter + "]");
				}
				converter.write(t, contentType, outputMessage);
				return;
			}
		}

		throw new RestClientException(
				"Could not extract response: no suitable HttpMessageConverter found "
						+ "for response type [" + t.getClass()
						+ "] and content type [" + contentType + "]");
	}

	protected MediaType getContentType(HttpInputMessage inputMessage) {
		MediaType contentType = inputMessage.getHeaders().getContentType();
		if (contentType == null) {
			if (logger.isTraceEnabled()) {
				logger.trace("No Content-Type header found, defaulting to application/octet-stream");
			}
			contentType = MediaType.APPLICATION_OCTET_STREAM;
		}
		return contentType;
	}
}

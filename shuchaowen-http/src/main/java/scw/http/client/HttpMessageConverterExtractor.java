package scw.http.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import scw.core.Assert;
import scw.http.MediaType;
import scw.http.converter.GenericHttpMessageConverter;
import scw.http.converter.HttpMessageConverter;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class HttpMessageConverterExtractor<T> implements ResponseExtractor<T> {

	private final Type responseType;

	private final Class<T> responseClass;

	private final List<HttpMessageConverter<?>> messageConverters;

	private final Logger logger;


	/**
	 * Create a new instance of the {@code HttpMessageConverterExtractor} with the given response
	 * type and message converters. The given converters must support the response type.
	 */
	public HttpMessageConverterExtractor(Class<T> responseType, List<HttpMessageConverter<?>> messageConverters) {
		this((Type) responseType, messageConverters);
	}

	/**
	 * Creates a new instance of the {@code HttpMessageConverterExtractor} with the given response
	 * type and message converters. The given converters must support the response type.
	 */
	public HttpMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters) {
		this(responseType, messageConverters, LoggerUtils.getLogger(HttpMessageConverterExtractor.class));
	}

	@SuppressWarnings("unchecked")
	HttpMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters, Logger logger) {
		Assert.notNull(responseType, "'responseType' must not be null");
		Assert.notEmpty(messageConverters, "'messageConverters' must not be empty");
		this.responseType = responseType;
		this.responseClass = (responseType instanceof Class ? (Class<T>) responseType : null);
		this.messageConverters = messageConverters;
		this.logger = logger;
	}


	@SuppressWarnings({"unchecked", "rawtypes", "resource"})
	public T extractData(ClientHttpResponse response) throws IOException {
		MessageBodyClientHttpResponseWrapper responseWrapper = new MessageBodyClientHttpResponseWrapper(response);
		if (!responseWrapper.hasMessageBody() || responseWrapper.hasEmptyMessageBody()) {
			return null;
		}
		MediaType contentType = getContentType(responseWrapper);

		for (HttpMessageConverter<?> messageConverter : this.messageConverters) {
			if (messageConverter instanceof GenericHttpMessageConverter) {
				GenericHttpMessageConverter<?> genericMessageConverter =
						(GenericHttpMessageConverter<?>) messageConverter;
				if (genericMessageConverter.canRead(this.responseType, null, contentType)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Reading [" + this.responseType + "] as \"" +
								contentType + "\" using [" + messageConverter + "]");
					}
					return (T) genericMessageConverter.read(this.responseType, null, responseWrapper);
				}
			}
			if (this.responseClass != null) {
				if (messageConverter.canRead(this.responseClass, contentType)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Reading [" + this.responseClass.getName() + "] as \"" +
								contentType + "\" using [" + messageConverter + "]");
					}
					return (T) messageConverter.read((Class) this.responseClass, responseWrapper);
				}
			}
		}

		throw new RestClientException("Could not extract response: no suitable HttpMessageConverter found " +
				"for response type [" + this.responseType + "] and content type [" + contentType + "]");
	}

	private MediaType getContentType(ClientHttpResponse response) {
		MediaType contentType = response.getHeaders().getContentType();
		if (contentType == null) {
			if (logger.isTraceEnabled()) {
				logger.trace("No Content-Type header found, defaulting to application/octet-stream");
			}
			contentType = MediaType.APPLICATION_OCTET_STREAM;
		}
		return contentType;
	}

}

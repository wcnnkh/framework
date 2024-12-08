package io.basc.framework.http.client;

import java.io.IOException;
import java.nio.charset.Charset;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.http.MediaType;
import io.basc.framework.util.io.IOUtils;

public class DefaultClientHttpResponseErrorHandler implements ClientHttpResponseErrorHandler {
	public static final ClientHttpResponseErrorHandler INSTANCE = new DefaultClientHttpResponseErrorHandler();

	/**
	 * Delegates to {@link #hasError(HttpStatus)} (for a standard status enum value)
	 * or {@link #hasError(int)} (for an unknown status code) with the response
	 * status code.
	 * 
	 * @see ClientHttpResponse#getRawStatusCode()
	 * @see #hasError(HttpStatus)
	 * @see #hasError(int)
	 */
	public boolean hasError(ClientHttpResponse response) throws IOException {
		int rawStatusCode = response.getRawStatusCode();
		HttpStatus httpStatus = HttpStatus.valueOf(rawStatusCode);
		if (httpStatus == null) {
			return hasError(rawStatusCode);
		}
		return hasError(httpStatus);
	}

	/**
	 * Template method called from {@link #hasError(ClientHttpResponse)}.
	 * <p>
	 * The default implementation checks if the given status code is
	 * {@link HttpStatus.Series#CLIENT_ERROR CLIENT_ERROR} or
	 * {@link HttpStatus.Series#SERVER_ERROR SERVER_ERROR}. Can be overridden in
	 * subclasses.
	 * 
	 * @param statusCode the HTTP status code as enum value
	 * @return {@code true} if the response indicates an error; {@code false}
	 *         otherwise
	 * @see HttpStatus#is4xxClientError()
	 * @see HttpStatus#is5xxServerError()
	 */
	protected boolean hasError(HttpStatus statusCode) {
		return (statusCode.is4xxClientError() || statusCode.is5xxServerError());
	}

	/**
	 * Template method called from {@link #hasError(ClientHttpResponse)}.
	 * <p>
	 * The default implementation checks if the given status code is
	 * {@code HttpStatus.Series#CLIENT_ERROR CLIENT_ERROR} or
	 * {@code HttpStatus.Series#SERVER_ERROR SERVER_ERROR}. Can be overridden in
	 * subclasses.
	 * 
	 * @param unknownStatusCode the HTTP status code as raw value
	 * @return {@code true} if the response indicates an error; {@code false}
	 *         otherwise
	 * @see HttpStatus.Series#CLIENT_ERROR
	 * @see HttpStatus.Series#SERVER_ERROR
	 */
	protected boolean hasError(int unknownStatusCode) {
		int seriesCode = unknownStatusCode / 100;
		return (seriesCode == HttpStatus.Series.CLIENT_ERROR.value()
				|| seriesCode == HttpStatus.Series.SERVER_ERROR.value());
	}

	/**
	 * This default implementation throws a {@link HttpClientErrorException} if the
	 * response status code is {@link HttpServerErrorException} if it is
	 */
	public void handleError(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = getHttpStatusCode(response);
		switch (statusCode.series()) {
		case CLIENT_ERROR:
			throw new HttpClientErrorException(statusCode, response.getStatusText(), response.getHeaders(),
					getResponseBody(response), getCharset(response));
		case SERVER_ERROR:
			throw new HttpServerErrorException(statusCode, response.getStatusText(), response.getHeaders(),
					getResponseBody(response), getCharset(response));
		default:
			throw new UnknownHttpStatusCodeException(statusCode.value(), response.getStatusText(),
					response.getHeaders(), getResponseBody(response), getCharset(response));
		}
	}

	/**
	 * Determine the HTTP status of the given response.
	 * <p>
	 * Note: Only called from {@link #handleError}, not from {@link #hasError}.
	 * 
	 * @param response the response to inspect
	 * @return the associated HTTP status
	 * @throws IOException                    in case of I/O errors
	 * @throws UnknownHttpStatusCodeException in case of an unknown status code that
	 *                                        cannot be represented with the
	 *                                        {@link HttpStatus} enum
	 */
	protected HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
		try {
			return response.getStatusCode();
		} catch (IllegalArgumentException ex) {
			throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(),
					response.getHeaders(), getResponseBody(response), getCharset(response));
		}
	}

	/**
	 * Read the body of the given response (for inclusion in a status exception).
	 * 
	 * @param response the response to inspect
	 * @return the response body as a byte array, or an empty byte array if the body
	 *         could not be read
	 */
	protected byte[] getResponseBody(ClientHttpResponse response) {
		try {
			return IOUtils.copyToByteArray(response.getInputStream());
		} catch (IOException ex) {
			// ignore
		}
		return new byte[0];
	}

	/**
	 * Determine the charset of the response (for inclusion in a status exception).
	 * 
	 * @param response the response to inspect
	 * @return the associated charset, or {@code null} if none
	 */
	protected Charset getCharset(ClientHttpResponse response) {
		HttpHeaders headers = response.getHeaders();
		MediaType contentType = headers.getContentType();
		return (contentType != null ? contentType.getCharset() : null);
	}

}
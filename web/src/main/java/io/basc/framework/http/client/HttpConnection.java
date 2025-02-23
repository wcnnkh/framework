package io.basc.framework.http.client;

import java.io.File;
import java.nio.charset.Charset;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpRequest;
import io.basc.framework.http.HttpRequestEntity;
import io.basc.framework.http.HttpRequestEntity.HeadersBuilder;
import io.basc.framework.net.MediaType;
import io.basc.framework.http.HttpResponseEntity;

public interface HttpConnection
		extends HttpClientConfigurable<HttpConnection>, HttpRequest, HeadersBuilder<HttpConnection> {

	HttpConnection clone();

	/**
	 * Set the length of the body in bytes, as specified by the
	 * {@code Content-Length} header.
	 * 
	 * @param contentLength the content length
	 * @return this builder
	 * @see HttpHeaders#setContentLength(long)
	 */
	HttpConnection contentLength(long contentLength);

	/**
	 * Set the {@linkplain MediaType media type} of the body, as specified by the
	 * {@code Content-Type} header.
	 * 
	 * @param contentType the content type
	 * @return this builder
	 * @see HttpHeaders#setContentType(MediaType)
	 */
	HttpConnection contentType(MediaType contentType);

	default HttpConnection contentType(MediaType contentType, Charset charset) {
		return contentType(new MediaType(contentType, charset));
	}

	default HttpConnection contentType(MediaType contentType, String charsetName) {
		return contentType(new MediaType(contentType, charsetName));
	}

	/**
	 * Set the body of the request entity and build the RequestEntity.
	 * 
	 * @param body the body of the request entity
	 * @return the built request entity
	 */
	default HttpConnection body(Object body) {
		return body(body, null);
	}

	/**
	 * Set the body and type of the request entity and build the RequestEntity.
	 * 
	 * @param body           the body of the request entity
	 * @param typeDescriptor the type of the body, useful for generic type
	 *                       resolution
	 * @return the built request entity
	 */
	HttpConnection body(Object body, TypeDescriptor typeDescriptor);

	TypeDescriptor getTypeDescriptor();

	Object getBody();

	default boolean hasBody() {
		return getBody() != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	default <T> HttpRequestEntity<T> build() {
		return new HttpRequestEntity<T>((T) getBody(), getHeaders(), getRawMethod(), getURI(), getTypeDescriptor());
	}

	<T> HttpResponseEntity<T> execute(ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException;

	default <T> HttpResponseEntity<T> execute(Class<T> responseType) throws HttpClientException {
		return execute(TypeDescriptor.valueOf(responseType));
	}

	<T> HttpResponseEntity<T> execute(TypeDescriptor responseType) throws HttpClientException;

	/**
	 * 虽然返回的文件会自动删除(并非完全可靠的),但是推荐在使用完后手动删除
	 * 
	 * @return
	 */
	default HttpResponseEntity<File> download() {
		return execute(DownLoadResponseExtractor.INSTANCE);
	}
}

package run.soeasy.framework.http.client;

import java.io.File;
import java.nio.charset.Charset;

import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.http.HttpHeaders;
import run.soeasy.framework.http.HttpRequest;
import run.soeasy.framework.http.HttpRequestEntity;
import run.soeasy.framework.http.HttpResponseEntity;
import run.soeasy.framework.http.HttpRequestEntity.HeadersBuilder;
import run.soeasy.framework.net.MediaType;

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
	 * Set the body and type of the request entity and build the RequestEntity.
	 * 
	 * @param body           the body of the request entity
	 * @return the built request entity
	 */
	HttpConnection body(Source body);
	
	Source getBody();

	default boolean hasBody() {
		return getBody() != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	default <T> HttpRequestEntity<T> build() {
		return new HttpRequestEntity<T>(getBody(), getHeaders(), getRawMethod(), getURI());
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

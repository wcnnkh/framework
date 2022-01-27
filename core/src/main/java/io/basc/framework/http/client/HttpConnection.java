package io.basc.framework.http.client;

import java.io.File;
import java.net.CookieHandler;
import java.net.URI;
import java.nio.charset.Charset;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.exception.HttpClientException;
import io.basc.framework.lang.Nullable;

public interface HttpConnection extends HttpConnectionFactory {
	/**
	 * Add the given, single header value under the given name.
	 * 
	 * @param headerName   the header name
	 * @param headerValues the header value(s)
	 * @return this builder
	 * @see HttpHeaders#add(String, String)
	 */
	HttpConnection header(String headerName, String... headerValues);

	/**
	 * Copy the given headers into the entity's headers map.
	 * 
	 * @param headers the existing HttpHeaders to copy from
	 * @return this builder
	 * @see HttpHeaders#add(String, String)
	 */
	HttpConnection headers(@Nullable HttpHeaders headers);

	/**
	 * Set the list of acceptable {@linkplain MediaType media types}, as specified
	 * by the {@code Accept} header.
	 * 
	 * @param acceptableMediaTypes the acceptable media types
	 */
	HttpConnection accept(MediaType... acceptableMediaTypes);

	/**
	 * Set the list of acceptable {@linkplain Charset charsets}, as specified by the
	 * {@code Accept-Charset} header.
	 * 
	 * @param acceptableCharsets the acceptable charsets
	 */
	HttpConnection acceptCharset(Charset... acceptableCharsets);

	/**
	 * Set the value of the {@code If-Modified-Since} header.
	 * <p>
	 * The date should be specified as the number of milliseconds since January 1,
	 * 1970 GMT.
	 * 
	 * @param ifModifiedSince the new value of the header
	 */
	HttpConnection ifModifiedSince(long ifModifiedSince);

	/**
	 * Set the values of the {@code If-None-Match} header.
	 * 
	 * @param ifNoneMatches the new value of the header
	 */
	HttpConnection ifNoneMatch(String... ifNoneMatches);

	HttpHeaders getHeaders();

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
	 * @param <T>  the type of the body
	 * @param body the body of the request entity
	 * @return the built request entity
	 */
	HttpConnection body(Object body);

	/**
	 * Set the body and type of the request entity and build the RequestEntity.
	 * 
	 * @param <T>  the type of the body
	 * @param body the body of the request entity
	 * @param type the type of the body, useful for generic type resolution
	 * @return the built request entity
	 */
	HttpConnection body(Object body, @Nullable TypeDescriptor typeDescriptor);

	@Nullable
	TypeDescriptor getTypeDescriptor();

	Object getBody();

	HttpMethod getMethod();

	URI getURI();

	boolean isRedirectEnable();

	HttpConnection setRedirectEnable(boolean enable);

	RedirectManager getRedirectManager();

	HttpConnection setRedirectManager(RedirectManager redirectManager);
	
	CookieHandler getCookieHandler();
	
	HttpConnection setCookieHandler(CookieHandler cookieHandler);

	ClientHttpRequestFactory getRequestFactory();

	HttpConnection setRequestFactory(ClientHttpRequestFactory requestFactory);

	<T> HttpResponseEntity<T> execute(ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException;

	<T> HttpResponseEntity<T> execute(Class<T> responseType) throws HttpClientException;

	<T> HttpResponseEntity<T> execute(TypeDescriptor responseType) throws HttpClientException;

	/**
	 * 虽然返回的文件会自动删除(并非完全可靠的),但是推荐在使用完后手动删除
	 * 
	 * @return
	 */
	default HttpResponseEntity<File> download() {
		DownLoadResponseExtractor responseExtractor = new DownLoadResponseExtractor(getURI());
		return execute(responseExtractor);
	}
}

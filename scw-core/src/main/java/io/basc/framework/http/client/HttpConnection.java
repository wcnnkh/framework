package io.basc.framework.http.client;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.env.Sys;
import io.basc.framework.http.ContentDisposition;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpRequestEntity;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.exception.HttpClientException;
import io.basc.framework.io.FileUtils;
import io.basc.framework.io.support.TemporaryFile;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.InetUtils;
import io.basc.framework.util.XUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;

public interface HttpConnection extends HttpConnectionFactory {
	static final RedirectManager REDIRECT_MANAGER = Sys.env.getServiceLoader(
			RedirectManager.class,
			"scw.http.client.HttpConnection.DefaultRedirectManager").first();

	/**
	 * Add the given, single header value under the given name.
	 * 
	 * @param headerName
	 *            the header name
	 * @param headerValues
	 *            the header value(s)
	 * @return this builder
	 * @see HttpHeaders#add(String, String)
	 */
	HttpConnection header(String headerName, String... headerValues);

	/**
	 * Copy the given headers into the entity's headers map.
	 * 
	 * @param headers
	 *            the existing HttpHeaders to copy from
	 * @return this builder
	 * @see HttpHeaders#add(String, String)
	 */
	HttpConnection headers(@Nullable HttpHeaders headers);

	/**
	 * Set the list of acceptable {@linkplain MediaType media types}, as
	 * specified by the {@code Accept} header.
	 * 
	 * @param acceptableMediaTypes
	 *            the acceptable media types
	 */
	HttpConnection accept(MediaType... acceptableMediaTypes);

	/**
	 * Set the list of acceptable {@linkplain Charset charsets}, as specified by
	 * the {@code Accept-Charset} header.
	 * 
	 * @param acceptableCharsets
	 *            the acceptable charsets
	 */
	HttpConnection acceptCharset(Charset... acceptableCharsets);

	/**
	 * Set the value of the {@code If-Modified-Since} header.
	 * <p>
	 * The date should be specified as the number of milliseconds since January
	 * 1, 1970 GMT.
	 * 
	 * @param ifModifiedSince
	 *            the new value of the header
	 */
	HttpConnection ifModifiedSince(long ifModifiedSince);

	/**
	 * Set the values of the {@code If-None-Match} header.
	 * 
	 * @param ifNoneMatches
	 *            the new value of the header
	 */
	HttpConnection ifNoneMatch(String... ifNoneMatches);

	HttpHeaders getHeaders();

	/**
	 * Set the length of the body in bytes, as specified by the
	 * {@code Content-Length} header.
	 * 
	 * @param contentLength
	 *            the content length
	 * @return this builder
	 * @see HttpHeaders#setContentLength(long)
	 */
	HttpConnection contentLength(long contentLength);

	/**
	 * Set the {@linkplain MediaType media type} of the body, as specified by
	 * the {@code Content-Type} header.
	 * 
	 * @param contentType
	 *            the content type
	 * @return this builder
	 * @see HttpHeaders#setContentType(MediaType)
	 */
	HttpConnection contentType(MediaType contentType);
	
	default HttpConnection contentType(MediaType contentType, Charset charset) {
		return contentType(new MediaType(contentType, charset));
	}
	
	default HttpConnection contentType(MediaType contentType,
			String charsetName) {
		return contentType(new MediaType(contentType, charsetName));
	}

	/**
	 * Set the body of the request entity and build the RequestEntity.
	 * 
	 * @param <T>
	 *            the type of the body
	 * @param body
	 *            the body of the request entity
	 * @return the built request entity
	 */
	HttpConnection body(Object body);

	/**
	 * Set the body and type of the request entity and build the RequestEntity.
	 * 
	 * @param <T>
	 *            the type of the body
	 * @param body
	 *            the body of the request entity
	 * @param type
	 *            the type of the body, useful for generic type resolution
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

	ClientHttpRequestFactory getRequestFactory();

	HttpConnection setRequestFactory(ClientHttpRequestFactory requestFactory);

	<T> HttpResponseEntity<T> execute(
			ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException;

	<T> HttpResponseEntity<T> execute(Class<T> responseType)
			throws HttpClientException;

	<T> HttpResponseEntity<T> execute(TypeDescriptor responseType)
			throws HttpClientException;

	/**
	 * 虽然返回的文件会自动删除(并非完全可靠的),但是推荐在使用完后手动删除
	 * @return
	 */
	default HttpResponseEntity<File> download(){
		DownLoadResponseExtractor responseExtractor = new DownLoadResponseExtractor(
				getURI());
		return execute(responseExtractor);
	}

	interface RedirectManager {
		URI getRedirect(ClientHttpResponse response) throws IOException;

		URI getRedirect(HttpResponseEntity<?> responseEntity);
	}

	static class DefaultRedirectManager implements RedirectManager {

		public URI getRedirect(ClientHttpResponse response) throws IOException {
			return getLocation(response.getStatusCode(), response.getHeaders());
		}

		public URI getRedirect(HttpResponseEntity<?> responseEntity) {
			return getLocation(responseEntity.getStatusCode(),
					responseEntity.getHeaders());
		}

		public URI getLocation(HttpStatus statusCode, HttpHeaders httpHeaders) {
			// 重定向
			if (statusCode == HttpStatus.MOVED_PERMANENTLY
					|| statusCode == HttpStatus.FOUND) {
				URI location = httpHeaders.getLocation();
				if (location != null) {
					return location;
				}
				return location;
			}
			return null;
		}
	}

	static final class DownLoadResponseExtractor implements
			ClientHttpResponseExtractor<File> {
		private static Logger logger = LoggerFactory
				.getLogger(DownLoadResponseExtractor.class);
		private final URI url;

		public DownLoadResponseExtractor(URI url) {
			this.url = url;
		}

		public URI getUrl() {
			return url;
		}

		public File execute(ClientHttpResponse response) throws IOException {
			if (response.getStatusCode() != HttpStatus.OK
					&& response.getStatusCode() != HttpStatus.NOT_MODIFIED) {
				logger.error("Unable to download:{}, status:{}, statusText:{}",
						url, response.getRawStatusCode(),
						response.getStatusText());
				return null;
			}

			ContentDisposition contentDisposition = response.getHeaders()
					.getContentDisposition();
			String fileName = contentDisposition == null ? null
					: contentDisposition.getFilename();
			if (StringUtils.isEmpty(fileName)) {
				fileName = InetUtils.getFilename(url.getPath());
			}

			TemporaryFile file = new TemporaryFile(FileUtils.getTempDirectory() + File.separator + XUtils.getUUID() + File.separator + fileName);
			if (logger.isDebugEnabled()) {
				logger.debug("{} download to {}", url, file.getPath());
			}

			try {
				FileUtils.copyInputStreamToFile(response.getInputStream(), file);
			} finally {
				file.deleteOnExit();
			}
			return file;
		}
	}

	static abstract class AbstractHttpConnection extends AbstractHttpConnectionFactory implements HttpConnection,
			Cloneable {
		private HttpMethod method;
		private URI uri;
		private final HttpHeaders headers;
		@Nullable
		private Object body;
		@Nullable
		private TypeDescriptor typeDescriptor;
		private boolean redirectEnable = false;
		private ClientHttpRequestFactory requestFactory;
		private RedirectManager redirectManager;

		public AbstractHttpConnection() {
			this.headers = new HttpHeaders();
		}
		
		public AbstractHttpConnection(HttpMethod method, URI uri) {
			this.headers = new HttpHeaders();
			this.method = method;
			this.uri = uri;
		}

		public AbstractHttpConnection(AbstractHttpConnection httpConnection) {
			this.method = httpConnection.method;
			this.uri = httpConnection.uri;
			this.body = httpConnection.body;
			this.typeDescriptor = httpConnection.typeDescriptor;
			this.headers = new HttpHeaders(httpConnection.headers);
			this.redirectEnable = httpConnection.redirectEnable;
			this.requestFactory = httpConnection.requestFactory;
			this.redirectManager = httpConnection.redirectManager;
		}

		public ClientHttpRequestFactory getRequestFactory() {
			return requestFactory;
		}

		public HttpConnection setRequestFactory(
				ClientHttpRequestFactory requestFactory) {
			this.requestFactory = requestFactory;
			return this;
		}

		public RedirectManager getRedirectManager() {
			return redirectManager == null ? REDIRECT_MANAGER : redirectManager;
		}

		public HttpConnection setRedirectManager(RedirectManager redirectManager) {
			this.redirectManager = redirectManager;
			return this;
		}
		
		public HttpHeaders getHeaders() {
			return headers;
		}

		public URI getURI() {
			return uri;
		}

		public HttpMethod getMethod() {
			return method;
		}

		public HttpConnection createConnection() {
			if(uri == null && method == null){
				return this;
			}
			
			return clone();
		}

		public HttpConnection createConnection(HttpMethod method, URI uri) {
			if (this.method != null || this.uri != null) {
				// 创建一个新的
				AbstractHttpConnection connection = clone();
				connection.method = method;
				connection.uri = uri;
				return connection;
			}
			this.method = method;
			this.uri = uri;
			return this;
		}

		public boolean isRedirectEnable() {
			return redirectEnable;
		}

		public HttpConnection setRedirectEnable(boolean enable) {
			this.redirectEnable = enable;
			return this;
		}

		public HttpConnection header(String headerName, String... headerValues) {
			for (String headerValue : headerValues) {
				this.headers.add(headerName, headerValue);
			}
			return this;
		}

		public HttpConnection headers(@Nullable HttpHeaders headers) {
			if (headers != null) {
				this.headers.putAll(headers);
			}
			return this;
		}

		public HttpConnection accept(MediaType... acceptableMediaTypes) {
			this.headers.setAccept(Arrays.asList(acceptableMediaTypes));
			return this;
		}

		public HttpConnection acceptCharset(Charset... acceptableCharsets) {
			this.headers.setAcceptCharset(Arrays.asList(acceptableCharsets));
			return this;
		}

		public HttpConnection contentLength(long contentLength) {
			this.headers.setContentLength(contentLength);
			return this;
		}

		public HttpConnection contentType(MediaType contentType) {
			this.headers.setContentType(contentType);
			return this;
		}

		public HttpConnection ifNoneMatch(String... ifNoneMatches) {
			this.headers.setIfNoneMatch(Arrays.asList(ifNoneMatches));
			return this;
		}

		public HttpConnection ifModifiedSince(long ifModifiedSince) {
			this.headers.setIfModifiedSince(ifModifiedSince);
			return this;
		}

		public HttpConnection body(Object body) {
			this.body = body;
			return this;
		}

		public HttpConnection body(Object body, TypeDescriptor typeDescriptor) {
			this.body = body;
			this.typeDescriptor = typeDescriptor;
			return this;
		}

		public Object getBody() {
			return body;
		}

		public TypeDescriptor getTypeDescriptor() {
			if (typeDescriptor == null && body != null) {
				return TypeDescriptor.forObject(body);
			}
			return typeDescriptor;
		}

		@SuppressWarnings("unchecked")
		public <T> HttpRequestEntity<T> buildRequestEntity() {
			return (HttpRequestEntity<T>) HttpRequestEntity
					.method(getMethod(), getURI()).headers(getHeaders())
					.body(getBody(), getTypeDescriptor());
		}

		@Override
		public AbstractHttpConnection clone() {
			try {
				return (AbstractHttpConnection) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

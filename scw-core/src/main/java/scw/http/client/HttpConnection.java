package scw.http.client;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;

import scw.core.utils.StringUtils;
import scw.http.ContentDisposition;
import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.HttpRequestEntity;
import scw.http.HttpResponseEntity;
import scw.http.HttpStatus;
import scw.http.MediaType;
import scw.http.client.exception.HttpClientException;
import scw.instance.InstanceUtils;
import scw.io.FileUtils;
import scw.io.support.TemporaryFile;
import scw.lang.Nullable;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.InetUtils;
import scw.util.XUtils;

public interface HttpConnection extends HttpConnectionFactory {
	static final RedirectManager REDIRECT_MANAGER = InstanceUtils.loadService(
			RedirectManager.class,
			"scw.http.client.HttpConnection.DefaultRedirectManager");

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
	
	HttpConnection contentType(MediaType contentType, String charsetName);
	
	HttpConnection contentType(MediaType contentType, Charset charset);

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
	HttpConnection body(Object body, Type type);

	Type getType();

	Object getBody();

	HttpMethod getMethod();

	URI getUrl();

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

	<T> HttpResponseEntity<T> execute(Type responseType)
			throws HttpClientException;

	HttpResponseEntity<File> download();

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
		private static Logger logger = LoggerUtils
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

			TemporaryFile file = TemporaryFile.createInTempDirectory(XUtils
					.getUUID() + File.separator + fileName);
			if (logger.isDebugEnabled()) {
				logger.debug("{} download to {}", url, file.getPath());
			}

			FileUtils.copyInputStreamToFile(response.getBody(), file);
			// 设置在垃圾回收时也进行删除
			file.setDeleteOnFinalize(true);
			return file;
		}
	}

	static abstract class AbstractHttpConnection extends AbstractHttpConnectionFactory implements HttpConnection,
			Cloneable {
		private HttpMethod method;
		private URI url;
		private final HttpHeaders headers;
		@Nullable
		private Object body;
		@Nullable
		private Type type;
		private boolean redirectEnable = false;
		private ClientHttpRequestFactory requestFactory;
		private RedirectManager redirectManager;

		public AbstractHttpConnection() {
			this.headers = new HttpHeaders();
		}
		
		public AbstractHttpConnection(HttpMethod method, URI url) {
			this.headers = new HttpHeaders();
			this.method = method;
			this.url = url;
		}

		public AbstractHttpConnection(AbstractHttpConnection httpConnection) {
			this.method = httpConnection.method;
			this.url = httpConnection.url;
			this.body = httpConnection.body;
			this.type = httpConnection.type;
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
		
		public HttpConnection contentType(MediaType contentType, Charset charset) {
			return contentType(new MediaType(contentType, charset));
		}
		
		public HttpConnection contentType(MediaType contentType,
				String charsetName) {
			return contentType(new MediaType(contentType, charsetName));
		}

		public HttpHeaders getHeaders() {
			return headers;
		}

		public URI getUrl() {
			return url;
		}

		public HttpMethod getMethod() {
			return method;
		}

		public HttpConnection createConnection() {
			if(url == null && method == null){
				return this;
			}
			
			return clone();
		}

		public HttpConnection createConnection(HttpMethod method, URI url) {
			if (this.method != null || this.url != null) {
				// 创建一个新的
				AbstractHttpConnection connection = clone();
				connection.method = method;
				connection.url = url;
				return connection;
			}
			this.method = method;
			this.url = url;
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

		public HttpConnection body(Object body, Type type) {
			this.body = body;
			this.type = type;
			return this;
		}

		public Object getBody() {
			return body;
		}

		public Type getType() {
			if (type == null && body != null) {
				return body.getClass();
			}
			return type;
		}

		@SuppressWarnings("unchecked")
		public <T> HttpRequestEntity<T> buildRequestEntity() {
			return (HttpRequestEntity<T>) HttpRequestEntity
					.method(getMethod(), getUrl()).headers(getHeaders())
					.body(getBody(), getType());
		}

		@Override
		public AbstractHttpConnection clone() {
			try {
				return (AbstractHttpConnection) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}

		public final HttpResponseEntity<File> download() {
			DownLoadResponseExtractor responseExtractor = new DownLoadResponseExtractor(
					getUrl());
			return execute(responseExtractor);
		}
	}
}

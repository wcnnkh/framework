package run.soeasy.framework.messaging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.AbstractMultiValueMap;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.LinkedCaseInsensitiveMap;
import run.soeasy.framework.core.strings.StringUtils;

public class Headers extends AbstractMultiValueMap<String, String, Map<String, List<String>>> implements Serializable {
	/**
	 * The {@code Content-Length} header field name.
	 * 
	 * @see <a href="https://tools.ietf.org/html/rfc7230#section-3.3.2">Section
	 *      3.3.2 of RFC 7230</a>
	 */
	public static final String CONTENT_LENGTH = "Content-Length";

	/**
	 * The {@code Content-Type} header field name.
	 * 
	 * @see <a href= "https://tools.ietf.org/html/rfc7231#section-3.1.1.5">Section
	 *      3.1.1.5 of RFC 7231</a>
	 */
	public static final String CONTENT_TYPE = "Content-Type";

	/**
	 * The {@code Content-Disposition} header field name.
	 * 
	 * @see <a href="https://tools.ietf.org/html/rfc6266">RFC 6266</a>
	 */
	public static final String CONTENT_DISPOSITION = "Content-Disposition";

	private static final long serialVersionUID = 1L;
	public static final Headers EMPTY = new Headers(Collections.emptyMap(), true);

	private Map<String, List<String>> source;
	private boolean readyOnly;

	{
		setValuesCreator((key) -> new LinkedList<>());
	}

	/**
	 * @param caseSensitiveKey 是否区别大小写
	 */
	public Headers(boolean caseSensitiveKey) {
		if (caseSensitiveKey) {
			this.source = new LinkedHashMap<String, List<String>>(8);
		} else {
			this.source = new LinkedCaseInsensitiveMap<List<String>>(8, Locale.ENGLISH);
		}
	}

	public Headers(Map<String, List<String>> headers, boolean readyOnly) {
		this.source = readyOnly ? Collections.unmodifiableMap(headers) : headers;
		this.readyOnly = readyOnly;
	}

	/**
	 * 克隆一个新的
	 * 
	 * @param headers
	 */
	public Headers(Headers headers) {
		this.source = headers.readyOnly ? headers.source : CollectionUtils.clone(headers.source);
		this.readyOnly = headers.readyOnly;
	}

	@Override
	public Map<String, List<String>> getSource() {
		return source;
	}

	public void caseSensitiveKey(boolean caseSensitiveKey) {
		Map<String, List<String>> map = source;
		if (caseSensitiveKey) {
			if (source instanceof LinkedCaseInsensitiveMap) {
				map = new LinkedHashMap<String, List<String>>();
				map.putAll(source);
			}
		} else {
			if (!(source instanceof LinkedCaseInsensitiveMap)) {
				map = new LinkedCaseInsensitiveMap<List<String>>(source.size(), Locale.ENGLISH);
				map.putAll(source);
			}
		}
		this.source = map;
	}

	public final boolean isReadyOnly() {
		return readyOnly;
	}

	public final boolean isCaseSensitiveKey() {
		return !(source instanceof LinkedCaseInsensitiveMap);
	}

	public void readyOnly() {
		if (isReadyOnly()) {
			return;
		}
		this.readyOnly = true;
		this.source = Collections.unmodifiableMap(this.source);
	}

	public void readyOnly(boolean caseSensitiveKey) {
		caseSensitiveKey(caseSensitiveKey);
		readyOnly();
	}

	/**
	 * Return all values of a given header name, even if this header is set multiple
	 * times.
	 */
	public List<String> getValuesAsList(String headerName, String tokenize) {
		List<String> values = get(headerName);
		if (values != null) {
			List<String> result = new ArrayList<String>();
			for (String value : values) {
				if (value != null) {
					String[] tokens = StringUtils.tokenizeToArray(value, tokenize);
					for (String token : tokens) {
						result.add(token);
					}
				}
			}
			return result;
		}
		return Collections.emptyList();
	}

	/**
	 * Set the {@code Content-Disposition} header when creating a
	 * {@code "multipart/form-data"} request.
	 * 
	 * @param name     the control name
	 * @param filename the filename (may be {@code null})
	 */
	public void setContentDispositionFormData(String name, String filename) {
		Assert.notNull(name, "'name' must not be null");
		StringBuilder builder = new StringBuilder("form-data; name=\"");
		builder.append(name).append('\"');
		if (filename != null) {
			builder.append("; filename=\"");
			builder.append(filename).append('\"');
		}
		set(CONTENT_DISPOSITION, builder.toString());
	}

	/**
	 * Set the {@literal Content-Disposition} header.
	 * <p>
	 * This could be used on a response to indicate if the content is expected to be
	 * displayed inline in the browser or as an attachment to be saved locally.
	 * <p>
	 * It can also be used for a {@code "multipart/form-data"} request. For more
	 * details see notes on {@link #setContentDispositionFormData}.
	 * 
	 * @see #getContentDisposition()
	 */
	public void setContentDisposition(ContentDisposition contentDisposition) {
		set(CONTENT_DISPOSITION, contentDisposition.toString());
	}

	/**
	 * Return a parsed representation of the {@literal Content-Disposition} header.
	 * 
	 * @see #setContentDisposition(ContentDisposition)
	 */
	public ContentDisposition getContentDisposition() {
		String contentDisposition = getFirst(CONTENT_DISPOSITION);
		if (contentDisposition != null) {
			return ContentDisposition.parse(contentDisposition);
		}
		return ContentDisposition.empty();
	}

	/**
	 * Set the length of the body in bytes, as specified by the
	 * {@code Content-Length} header.
	 */
	public void setContentLength(long contentLength) {
		set(CONTENT_LENGTH, Long.toString(contentLength));
	}

	/**
	 * Return the length of the body in bytes, as specified by the
	 * {@code Content-Length} header.
	 * <p>
	 * Returns -1 when the content-length is unknown.
	 */
	public long getContentLength() {
		String value = getFirst(CONTENT_LENGTH);
		return (value != null ? Long.parseLong(value) : -1);
	}

	/**
	 * Set the {@linkplain MediaType media type} of the body, as specified by the
	 * {@code Content-Type} header.
	 */
	public void setContentType(MediaType mediaType) {
		if (mediaType == null) {
			remove(CONTENT_TYPE);
			return;
		}

		Assert.isTrue(!mediaType.isWildcardType(), "Content-Type cannot contain wildcard type '*'");
		Assert.isTrue(!mediaType.isWildcardSubtype(), "Content-Type cannot contain wildcard subtype '*'");
		set(CONTENT_TYPE, mediaType.toString());
	}

	/**
	 * Return the {@linkplain MediaType media type} of the body, as specified by the
	 * {@code Content-Type} header.
	 * <p>
	 * Returns {@code null} when the content-type is unknown.
	 */
	public MediaType getContentType() {
		String value = getFirst(CONTENT_TYPE);
		return (StringUtils.isEmpty(value) ? null : MediaType.parseMediaType(value));
	}
}

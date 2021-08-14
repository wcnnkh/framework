package scw.net.uri;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import scw.codec.Encoder;
import scw.core.Constants;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.lang.Nullable;
import scw.mapper.MapperUtils;
import scw.util.MultiValueMap;
import scw.value.Value;

public class UriUtils {
	public static final String QUERY_CONNECTOR = "?";
	public static final String PARAMETER_CONNECTOR = "&";
	public static final String NAME_VALUE_CONNECTOR = "=";
	
	/**
	 * Encode the given URI scheme with the given encoding.
	 * 
	 * @param scheme
	 *            the scheme to be encoded
	 * @param encoding
	 *            the character encoding to encode to
	 * @return the encoded scheme
	 * @throws UnsupportedEncodingException
	 *             when the given encoding parameter is not supported
	 */
	public static String encodeScheme(String scheme, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(scheme, encoding, HierarchicalUriComponents.Type.SCHEME);
	}

	/**
	 * Encode the given URI authority with the given encoding.
	 * 
	 * @param authority
	 *            the authority to be encoded
	 * @param encoding
	 *            the character encoding to encode to
	 * @return the encoded authority
	 * @throws UnsupportedEncodingException
	 *             when the given encoding parameter is not supported
	 */
	public static String encodeAuthority(String authority, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(authority, encoding,
				HierarchicalUriComponents.Type.AUTHORITY);
	}

	/**
	 * Encode the given URI user info with the given encoding.
	 * 
	 * @param userInfo
	 *            the user info to be encoded
	 * @param encoding
	 *            the character encoding to encode to
	 * @return the encoded user info
	 * @throws UnsupportedEncodingException
	 *             when the given encoding parameter is not supported
	 */
	public static String encodeUserInfo(String userInfo, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(userInfo, encoding,
				HierarchicalUriComponents.Type.USER_INFO);
	}

	/**
	 * Encode the given URI host with the given encoding.
	 * 
	 * @param host
	 *            the host to be encoded
	 * @param encoding
	 *            the character encoding to encode to
	 * @return the encoded host
	 * @throws UnsupportedEncodingException
	 *             when the given encoding parameter is not supported
	 */
	public static String encodeHost(String host, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(host, encoding, HierarchicalUriComponents.Type.HOST_IPV4);
	}

	/**
	 * Encode the given URI port with the given encoding.
	 * 
	 * @param port
	 *            the port to be encoded
	 * @param encoding
	 *            the character encoding to encode to
	 * @return the encoded port
	 * @throws UnsupportedEncodingException
	 *             when the given encoding parameter is not supported
	 */
	public static String encodePort(String port, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(port, encoding, HierarchicalUriComponents.Type.PORT);
	}

	/**
	 * Encode the given URI path with the given encoding.
	 * 
	 * @param path
	 *            the path to be encoded
	 * @param encoding
	 *            the character encoding to encode to
	 * @return the encoded path
	 * @throws UnsupportedEncodingException
	 *             when the given encoding parameter is not supported
	 */
	public static String encodePath(String path, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(path, encoding, HierarchicalUriComponents.Type.PATH);
	}

	/**
	 * Encode the given URI path segment with the given encoding.
	 * 
	 * @param segment
	 *            the segment to be encoded
	 * @param encoding
	 *            the character encoding to encode to
	 * @return the encoded segment
	 * @throws UnsupportedEncodingException
	 *             when the given encoding parameter is not supported
	 */
	public static String encodePathSegment(String segment, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(segment, encoding,
				HierarchicalUriComponents.Type.PATH_SEGMENT);
	}

	/**
	 * Encode the given URI query with the given encoding.
	 * 
	 * @param query
	 *            the query to be encoded
	 * @param encoding
	 *            the character encoding to encode to
	 * @return the encoded query
	 * @throws UnsupportedEncodingException
	 *             when the given encoding parameter is not supported
	 */
	public static String encodeQuery(String query, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(query, encoding, HierarchicalUriComponents.Type.QUERY);
	}

	/**
	 * Encode the given URI query parameter with the given encoding.
	 * 
	 * @param queryParam
	 *            the query parameter to be encoded
	 * @param encoding
	 *            the character encoding to encode to
	 * @return the encoded query parameter
	 * @throws UnsupportedEncodingException
	 *             when the given encoding parameter is not supported
	 */
	public static String encodeQueryParam(String queryParam, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(queryParam, encoding,
				HierarchicalUriComponents.Type.QUERY_PARAM);
	}

	/**
	 * Encode the given URI fragment with the given encoding.
	 * 
	 * @param fragment
	 *            the fragment to be encoded
	 * @param encoding
	 *            the character encoding to encode to
	 * @return the encoded fragment
	 * @throws UnsupportedEncodingException
	 *             when the given encoding parameter is not supported
	 */
	public static String encodeFragment(String fragment, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(fragment, encoding,
				HierarchicalUriComponents.Type.FRAGMENT);
	}

	/**
	 * Extract the file extension from the given URI path.
	 * 
	 * @param path
	 *            the URI path (e.g. "/products/index.html")
	 * @return the extracted file extension (e.g. "html")
	 */
	public static String extractFileExtension(String path) {
		int end = path.indexOf('?');
		int fragmentIndex = path.indexOf('#');
		if (fragmentIndex != -1 && (end == -1 || fragmentIndex < end)) {
			end = fragmentIndex;
		}
		if (end == -1) {
			end = path.length();
		}
		int begin = path.lastIndexOf('/', end) + 1;
		int paramIndex = path.indexOf(';', begin);
		end = (paramIndex != -1 && paramIndex < end ? paramIndex : end);
		int extIndex = path.lastIndexOf('.', end);
		if (extIndex != -1 && extIndex > begin) {
			return path.substring(extIndex + 1, end);
		}
		return null;
	}

	/**
	 * 将一段uri转换成键值对
	 * 
	 * @param content
	 * @return
	 */
	public static MultiValueMap<String, String> getQueryParams(String uri) {
		if (!StringUtils.hasText(uri)) {
			return CollectionUtils.emptyMultiValueMap();
		}

		int index = uri.indexOf("?");
		String queryString = index == -1 ? uri : uri.substring(index + 1);
		index = queryString.indexOf("#");
		if (index != -1) {
			queryString = queryString.substring(0, index - 1);
		}
		return UriComponentsBuilder.newInstance().query(queryString).build().getQueryParams();
	}
	
	public static String toQueryString(Object body, @Nullable Encoder<String, String> encoder) {
		return toQueryString(body, encoder, PARAMETER_CONNECTOR, NAME_VALUE_CONNECTOR);
	}

	public static String toQueryString(Object body, @Nullable Encoder<String, String> encoder, @Nullable String parameterConnector, @Nullable String nameValueConnector) {
		if (body == null) {
			return null;
		}

		if (Value.isBaseType(body.getClass())) {
			return body.toString();
		}

		return toQueryString(MapperUtils.toMap(body), encoder, parameterConnector, nameValueConnector);
	}

	@SuppressWarnings("rawtypes")
	private static String toQueryString(String key, Collection values, @Nullable Encoder<String, String> encoder, @Nullable String parameterConnector, @Nullable String nameValueConnector) {
		if (StringUtils.isEmpty(key) || CollectionUtils.isEmpty(values)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (Object value : values) {
			if (value == null) {
				continue;
			}

			if (sb.length() > 0 && parameterConnector != null) {
				sb.append(parameterConnector);
			}

			sb.append(key);
			if(nameValueConnector != null){
				sb.append(nameValueConnector);
			}
			if (encoder != null) {
				sb.append(encoder.encode(value.toString()));
			} else {
				sb.append(value.toString());
			}
		}
		return sb.toString();
	}
	
	@SuppressWarnings("rawtypes")
	public static String toQueryString(Map parameterMap, @Nullable Encoder<String, String> encoder){
		return toQueryString(parameterMap, encoder, PARAMETER_CONNECTOR, NAME_VALUE_CONNECTOR);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String toQueryString(Map parameterMap, @Nullable Encoder<String, String> encoder, @Nullable String parameterConnector, @Nullable String nameValueConnector) {
		if (CollectionUtils.isEmpty(parameterMap)) {
			return null;
		}
		
		Map parameters = CollectionUtils.sort(parameterMap);
		StringBuilder sb = new StringBuilder();
		Set<Entry> entries = parameters.entrySet();
		for (Map.Entry entry : entries) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			String key = entry.getKey().toString();
			String text;
			if (value instanceof Collection) {
				text = toQueryString(key, (Collection) value, encoder, parameterConnector, nameValueConnector);
			} else if (value.getClass().isArray()) {
				text = toQueryString(key, ArrayUtils.toList(value), encoder, parameterConnector, nameValueConnector);
			} else {
				text = toQueryString(key, Arrays.asList(value), encoder, parameterConnector, nameValueConnector);
			}

			if (text == null) {
				continue;
			}

			if (sb.length() != 0 && parameterConnector != null) {
				sb.append(parameterConnector);
			}

			sb.append(text);
		}
		return sb.toString();
	}

	/**
	 * 在url后面追加参数
	 * 
	 * @param url
	 * @param paramMap
	 * @param charsetName
	 * @return
	 */
	public static String appendQueryParams(String url, Map<String, ?> paramMap, @Nullable Encoder<String, String> encoder) {
		if (paramMap == null || paramMap.isEmpty()) {
			return url;
		}

		StringBuilder sb = new StringBuilder(128);
		if (!StringUtils.isEmpty(url)) {
			sb.append(url);
			if (url.lastIndexOf(QUERY_CONNECTOR) == -1) {
				sb.append(QUERY_CONNECTOR);
			} else {
				sb.append(PARAMETER_CONNECTOR);
			}
		}

		String text = toQueryString(paramMap, encoder, PARAMETER_CONNECTOR, NAME_VALUE_CONNECTOR);
		if (text != null) {
			sb.append(text);
		}
		return sb.toString();
	}

	/**
	 * @param source
	 * @param charsetName
	 * @return
	 * @see URLEncoder#encode(java.lang.String, java.lang.String)
	 */
	public static String encode(String source, String charsetName) {
		if (source == null) {
			return null;
		}

		try {
			return URLEncoder.encode(source, charsetName);
		} catch (UnsupportedEncodingException e) {
			// Should never happen
			throw new IllegalStateException("Failed to encode URI variable", e);
		}
	}

	/**
	 * The World Wide Web Consortium Recommendation states that UTF-8 should be
	 * used.
	 * 
	 * @param source
	 * @return
	 */
	public static String encode(String source) {
		return encode(source, Constants.UTF_8_NAME);
	}

	/**
	 * @param source
	 * @param charsetName
	 * @return
	 * @see URLEncoder#decode(java.lang.String, java.lang.String)
	 */
	public static String decode(String source, String charsetName) {
		if (source == null) {
			return null;
		}

		try {
			return URLDecoder.decode(source, charsetName);
		} catch (UnsupportedEncodingException e) {
			// Should never happen
			throw new IllegalStateException("Failed to decode URI variable", e);
		}
	}

	/**
	 * The World Wide Web Consortium Recommendation states that UTF-8 should be
	 * used.
	 * 
	 * @param source
	 * @return
	 */
	public static String decode(String source) {
		return decode(source, Constants.UTF_8_NAME);
	}

	public static boolean isUri(String uri) {
		if(StringUtils.isEmpty(uri)){
			return false;
		}
		
		try {
			new URI(uri);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}
	
	public static URI toUri(String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Failed to URI [" + uri + "]", e);
		}
	}
}

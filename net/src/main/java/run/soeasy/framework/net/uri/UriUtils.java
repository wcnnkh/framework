package run.soeasy.framework.net.uri;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import run.soeasy.framework.core.Constants;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.collections.CollectionUtils;
import run.soeasy.framework.util.collections.MultiValueMap;

public class UriUtils {
	public static final String QUERY_CONNECTOR = "?";
	public static final String PARAMETER_CONNECTOR = "&";
	public static final String NAME_VALUE_CONNECTOR = "=";

	/**
	 * Encode the given URI scheme with the given encoding.
	 * 
	 * @param scheme   the scheme to be encoded
	 * @param encoding the character encoding to encode to
	 * @return the encoded scheme
	 * @throws UnsupportedEncodingException when the given encoding parameter is not
	 *                                      supported
	 */
	public static String encodeScheme(String scheme, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(scheme, encoding, HierarchicalUriComponents.Type.SCHEME);
	}

	/**
	 * Encode the given URI authority with the given encoding.
	 * 
	 * @param authority the authority to be encoded
	 * @param encoding  the character encoding to encode to
	 * @return the encoded authority
	 * @throws UnsupportedEncodingException when the given encoding parameter is not
	 *                                      supported
	 */
	public static String encodeAuthority(String authority, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(authority, encoding,
				HierarchicalUriComponents.Type.AUTHORITY);
	}

	/**
	 * Encode the given URI user info with the given encoding.
	 * 
	 * @param userInfo the user info to be encoded
	 * @param encoding the character encoding to encode to
	 * @return the encoded user info
	 * @throws UnsupportedEncodingException when the given encoding parameter is not
	 *                                      supported
	 */
	public static String encodeUserInfo(String userInfo, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(userInfo, encoding,
				HierarchicalUriComponents.Type.USER_INFO);
	}

	/**
	 * Encode the given URI host with the given encoding.
	 * 
	 * @param host     the host to be encoded
	 * @param encoding the character encoding to encode to
	 * @return the encoded host
	 * @throws UnsupportedEncodingException when the given encoding parameter is not
	 *                                      supported
	 */
	public static String encodeHost(String host, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(host, encoding, HierarchicalUriComponents.Type.HOST_IPV4);
	}

	/**
	 * Encode the given URI port with the given encoding.
	 * 
	 * @param port     the port to be encoded
	 * @param encoding the character encoding to encode to
	 * @return the encoded port
	 * @throws UnsupportedEncodingException when the given encoding parameter is not
	 *                                      supported
	 */
	public static String encodePort(String port, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(port, encoding, HierarchicalUriComponents.Type.PORT);
	}

	/**
	 * Encode the given URI path with the given encoding.
	 * 
	 * @param path     the path to be encoded
	 * @param encoding the character encoding to encode to
	 * @return the encoded path
	 * @throws UnsupportedEncodingException when the given encoding parameter is not
	 *                                      supported
	 */
	public static String encodePath(String path, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(path, encoding, HierarchicalUriComponents.Type.PATH);
	}

	/**
	 * Encode the given URI path segment with the given encoding.
	 * 
	 * @param segment  the segment to be encoded
	 * @param encoding the character encoding to encode to
	 * @return the encoded segment
	 * @throws UnsupportedEncodingException when the given encoding parameter is not
	 *                                      supported
	 */
	public static String encodePathSegment(String segment, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(segment, encoding,
				HierarchicalUriComponents.Type.PATH_SEGMENT);
	}

	/**
	 * Encode the given URI query with the given encoding.
	 * 
	 * @param query    the query to be encoded
	 * @param encoding the character encoding to encode to
	 * @return the encoded query
	 * @throws UnsupportedEncodingException when the given encoding parameter is not
	 *                                      supported
	 */
	public static String encodeQuery(String query, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(query, encoding, HierarchicalUriComponents.Type.QUERY);
	}

	/**
	 * Encode the given URI query parameter with the given encoding.
	 * 
	 * @param queryParam the query parameter to be encoded
	 * @param encoding   the character encoding to encode to
	 * @return the encoded query parameter
	 * @throws UnsupportedEncodingException when the given encoding parameter is not
	 *                                      supported
	 */
	public static String encodeQueryParam(String queryParam, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(queryParam, encoding,
				HierarchicalUriComponents.Type.QUERY_PARAM);
	}

	/**
	 * Encode the given URI fragment with the given encoding.
	 * 
	 * @param fragment the fragment to be encoded
	 * @param encoding the character encoding to encode to
	 * @return the encoded fragment
	 * @throws UnsupportedEncodingException when the given encoding parameter is not
	 *                                      supported
	 */
	public static String encodeFragment(String fragment, String encoding) throws UnsupportedEncodingException {
		return HierarchicalUriComponents.encodeUriComponent(fragment, encoding,
				HierarchicalUriComponents.Type.FRAGMENT);
	}

	/**
	 * Extract the file extension from the given URI path.
	 * 
	 * @param path the URI path (e.g. "/products/index.html")
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

	public static String encode(String source) {
		return encode(source, Constants.UTF_8_NAME);
	}

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

	public static String decode(String source) {
		return decode(source, Constants.UTF_8_NAME);
	}

	public static boolean isUri(String uri) {
		if (StringUtils.isEmpty(uri)) {
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

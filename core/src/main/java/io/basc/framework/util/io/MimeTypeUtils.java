package io.basc.framework.util.io;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.basc.framework.core.Constants;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.io.MimeType.SpecificityComparator;

public final class MimeTypeUtils {
	private static final byte[] BOUNDARY_CHARS = new byte[] { '-', '_', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
			'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	private static final Random RND = new SecureRandom();

	public static Charset US_ASCII = Constants.US_ASCII;

	/**
	 * Comparator used by {@link #sortBySpecificity(List)}.
	 */
	public static final Comparator<MimeType> SPECIFICITY_COMPARATOR = new SpecificityComparator<MimeType>();

	/**
	 * Public constant mime type that includes all media ranges (i.e.
	 * "&#42;/&#42;").
	 */
	public static final MimeType ALL;

	/**
	 * A String equivalent of {@link MimeTypeUtils#ALL}.
	 */
	public static final String ALL_VALUE = "*/*";

	/**
	 * Public constant mime type for {@code application/atom+xml}.
	 */
	public static final MimeType APPLICATION_ATOM_XML;

	/**
	 * A String equivalent of {@link MimeTypeUtils#APPLICATION_ATOM_XML}.
	 */
	public static final String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";

	/**
	 * JSON规范类型为{@link MimeTypeUtils#APPLICATION_JSON}
	 */
	public static final MimeType TEXT_JSON = new MimeType("text", "json");

	/**
	 * JSON规范类型为{@link MimeTypeUtils#APPLICATION_JSON_VALUE}
	 */
	public static final String TEXT_JSON_VALUE = "text/json";

	/**
	 * Public constant mime type for {@code application/json}.
	 */
	public static final MimeType APPLICATION_JSON;

	/**
	 * A String equivalent of {@link MimeTypeUtils#APPLICATION_JSON}.
	 */
	public static final String APPLICATION_JSON_VALUE = "application/json";

	/**
	 * Public constant mime type for {@code application/octet-stream}.
	 */
	public static final MimeType APPLICATION_OCTET_STREAM;

	/**
	 * A String equivalent of {@link MimeTypeUtils#APPLICATION_OCTET_STREAM}.
	 */
	public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

	/**
	 * Public constant mime type for {@code application/xhtml+xml}.
	 */
	public static final MimeType APPLICATION_XHTML_XML;

	/**
	 * A String equivalent of {@link MimeTypeUtils#APPLICATION_XHTML_XML}.
	 */
	public static final String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";

	/**
	 * Public constant mime type for {@code application/xml}.
	 */
	public static final MimeType APPLICATION_XML;

	/**
	 * A String equivalent of {@link MimeTypeUtils#APPLICATION_XML}.
	 */
	public static final String APPLICATION_XML_VALUE = "application/xml";

	/**
	 * Public constant mime type for {@code image/gif}.
	 */
	public static final MimeType IMAGE_GIF;

	/**
	 * A String equivalent of {@link MimeTypeUtils#IMAGE_GIF}.
	 */
	public static final String IMAGE_GIF_VALUE = "image/gif";

	/**
	 * Public constant mime type for {@code image/jpeg}.
	 */
	public static final MimeType IMAGE_JPEG;

	/**
	 * A String equivalent of {@link MimeTypeUtils#IMAGE_JPEG}.
	 */
	public static final String IMAGE_JPEG_VALUE = "image/jpeg";

	/**
	 * Public constant mime type for {@code image/png}.
	 */
	public static final MimeType IMAGE_PNG;

	/**
	 * A String equivalent of {@link MimeTypeUtils#IMAGE_PNG}.
	 */
	public static final String IMAGE_PNG_VALUE = "image/png";

	/**
	 * Public constant mime type for {@code text/html}.
	 */
	public static final MimeType TEXT_HTML;

	/**
	 * A String equivalent of {@link MimeTypeUtils#TEXT_HTML}.
	 */
	public static final String TEXT_HTML_VALUE = "text/html";

	/**
	 * Public constant mime type for {@code text/plain}.
	 */
	public static final MimeType TEXT_PLAIN;

	/**
	 * A String equivalent of {@link MimeTypeUtils#TEXT_PLAIN}.
	 */
	public static final String TEXT_PLAIN_VALUE = "text/plain";

	/**
	 * Public constant mime type for {@code text/xml}.
	 */
	public static final MimeType TEXT_XML;

	/**
	 * A String equivalent of {@link MimeTypeUtils#TEXT_XML}.
	 */
	public static final String TEXT_XML_VALUE = "text/xml";

	public static final MimeType TEXT_JAVASCRIPT = new MimeType("text", "javascript");

	public static final String TEXT_JAVASCRIPT_VALUE = "text/javascript";

	static {
		ALL = MimeType.valueOf(ALL_VALUE);
		APPLICATION_ATOM_XML = MimeType.valueOf(APPLICATION_ATOM_XML_VALUE);
		APPLICATION_JSON = MimeType.valueOf(APPLICATION_JSON_VALUE);
		APPLICATION_OCTET_STREAM = MimeType.valueOf(APPLICATION_OCTET_STREAM_VALUE);
		APPLICATION_XHTML_XML = MimeType.valueOf(APPLICATION_XHTML_XML_VALUE);
		APPLICATION_XML = MimeType.valueOf(APPLICATION_XML_VALUE);
		IMAGE_GIF = MimeType.valueOf(IMAGE_GIF_VALUE);
		IMAGE_JPEG = MimeType.valueOf(IMAGE_JPEG_VALUE);
		IMAGE_PNG = MimeType.valueOf(IMAGE_PNG_VALUE);
		TEXT_HTML = MimeType.valueOf(TEXT_HTML_VALUE);
		TEXT_PLAIN = MimeType.valueOf(TEXT_PLAIN_VALUE);
		TEXT_XML = MimeType.valueOf(TEXT_XML_VALUE);
	}

	/**
	 * Parse the given String into a single {@code MimeType}.
	 * 
	 * @param mimeType the string to parse
	 * @return the mime type
	 * @throws InvalidMimeTypeException if the string cannot be parsed
	 */
	public static MimeType parseMimeType(String mimeType) {
		if (StringUtils.isEmpty(mimeType)) {
			throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
		}

		int index = mimeType.indexOf(';');
		String fullType = (index >= 0 ? mimeType.substring(0, index) : mimeType).trim();
		if (fullType.isEmpty()) {
			throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
		}

		// java.net.HttpURLConnection returns a *; q=.2 Accept header
		if (MimeType.WILDCARD_TYPE.equals(fullType)) {
			fullType = "*/*";
		}
		int subIndex = fullType.indexOf('/');
		if (subIndex == -1) {
			throw new InvalidMimeTypeException(mimeType, "does not contain '/'");
		}
		if (subIndex == fullType.length() - 1) {
			throw new InvalidMimeTypeException(mimeType, "does not contain subtype after '/'");
		}
		String type = fullType.substring(0, subIndex);
		String subtype = fullType.substring(subIndex + 1, fullType.length());
		if (MimeType.WILDCARD_TYPE.equals(type) && !MimeType.WILDCARD_TYPE.equals(subtype)) {
			throw new InvalidMimeTypeException(mimeType, "wildcard type is legal only in '*/*' (all mime types)");
		}

		Map<String, String> parameters = null;
		do {
			int nextIndex = index + 1;
			boolean quoted = false;
			while (nextIndex < mimeType.length()) {
				char ch = mimeType.charAt(nextIndex);
				if (ch == ';') {
					if (!quoted) {
						break;
					}
				} else if (ch == '"') {
					quoted = !quoted;
				}
				nextIndex++;
			}
			String parameter = mimeType.substring(index + 1, nextIndex).trim();
			if (parameter.length() > 0) {
				if (parameters == null) {
					parameters = new LinkedHashMap<String, String>(4);
				}
				int eqIndex = parameter.indexOf('=');
				if (eqIndex >= 0) {
					String attribute = parameter.substring(0, eqIndex).trim();
					String value = parameter.substring(eqIndex + 1, parameter.length()).trim();
					parameters.put(attribute, value);
				}
			}
			index = nextIndex;
		} while (index < mimeType.length());

		try {
			return new MimeType(type, subtype, parameters);
		} catch (UnsupportedCharsetException ex) {
			throw new InvalidMimeTypeException(mimeType, "unsupported charset '" + ex.getCharsetName() + "'");
		} catch (IllegalArgumentException ex) {
			throw new InvalidMimeTypeException(mimeType, ex.getMessage());
		}
	}

	/**
	 * Parse the given, comma-separated string into a list of {@code MimeType}
	 * objects.
	 * 
	 * @param mimeTypes the string to parse
	 * @return the list of mime types
	 * @throws IllegalArgumentException if the string cannot be parsed
	 */
	public static List<MimeType> parseMimeTypes(String mimeTypes) {
		if (StringUtils.isEmpty(mimeTypes)) {
			return Collections.emptyList();
		}
		String[] tokens = StringUtils.tokenizeToArray(mimeTypes, ",");
		List<MimeType> result = new ArrayList<MimeType>(tokens.length);
		for (String token : tokens) {
			result.add(parseMimeType(token));
		}
		return result;
	}

	/**
	 * Return a string representation of the given list of {@code MimeType} objects.
	 * 
	 * @param mimeTypes the string to parse
	 * @return the list of mime types
	 * @throws IllegalArgumentException if the String cannot be parsed
	 */
	public static String toString(Iterable<? extends MimeType> mimeTypes) {
		StringBuilder builder = new StringBuilder();
		for (Iterator<? extends MimeType> iterator = mimeTypes.iterator(); iterator.hasNext();) {
			MimeType mimeType = iterator.next();
			mimeType.appendTo(builder);
			if (iterator.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}

	/**
	 * Sorts the given list of {@code MimeType} objects by specificity.
	 * <p>
	 * Given two mime types:
	 * <ol>
	 * <li>if either mime type has a {@linkplain MimeType#isWildcardType() wildcard
	 * type}, then the mime type without the wildcard is ordered before the
	 * other.</li>
	 * <li>if the two mime types have different {@linkplain MimeType#getType()
	 * types}, then they are considered equal and remain their current order.</li>
	 * <li>if either mime type has a {@linkplain MimeType#isWildcardSubtype()
	 * wildcard subtype} , then the mime type without the wildcard is sorted before
	 * the other.</li>
	 * <li>if the two mime types have different {@linkplain MimeType#getSubtype()
	 * subtypes}, then they are considered equal and remain their current
	 * order.</li>
	 * <li>if the two mime types have a different amount of
	 * {@linkplain MimeType#getParameter(String) parameters}, then the mime type
	 * with the most parameters is ordered before the other.</li>
	 * </ol>
	 * <p>
	 * For example: <blockquote>audio/basic &lt; audio/* &lt; *&#047;*</blockquote>
	 * <blockquote>audio/basic;level=1 &lt; audio/basic</blockquote>
	 * <blockquote>audio/basic == text/html</blockquote> <blockquote>audio/basic ==
	 * audio/wave</blockquote>
	 * 
	 * @param mimeTypes the list of mime types to be sorted
	 * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.3.2">HTTP 1.1:
	 *      Semantics and Content, section 5.3.2</a>
	 */
	public static void sortBySpecificity(List<MimeType> mimeTypes) {
		Assert.notNull(mimeTypes, "'mimeTypes' must not be null");
		if (mimeTypes.size() > 1) {
			Collections.sort(mimeTypes, SPECIFICITY_COMPARATOR);
		}
	}

	public static byte[] generateMultipartBoundary() {
		byte[] boundary = new byte[RND.nextInt(11) + 30];
		for (int i = 0; i < boundary.length; i++) {
			boundary[i] = BOUNDARY_CHARS[RND.nextInt(BOUNDARY_CHARS.length)];
		}
		return boundary;
	}

	public static List<MimeType> parseMimeTypes(List<String> mimeTypes) {
		if (CollectionUtils.isEmpty(mimeTypes)) {
			return Collections.<MimeType>emptyList();
		} else if (mimeTypes.size() == 1) {
			return parseMimeTypes(mimeTypes.get(0));
		} else {
			List<MimeType> result = new ArrayList<MimeType>(8);
			for (String mediaType : mimeTypes) {
				result.addAll(parseMimeTypes(mediaType));
			}
			return result;
		}
	}

	public static String generateMultipartBoundaryString() {
		return new String(generateMultipartBoundary(), US_ASCII);
	}

}

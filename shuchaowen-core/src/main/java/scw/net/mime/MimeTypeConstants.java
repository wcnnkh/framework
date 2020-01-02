package scw.net.mime;

import java.util.Comparator;
import java.util.List;

public interface MimeTypeConstants {
	static final String WILDCARD_TYPE = "*";

	static final String PARAM_CHARSET = "charset";

	static final String WILDCARD_SUBTYPE_PREFIX = "*+";

	static final String TYPE_SPLIT = "/";

	static final String PARAMETER_SPLIT = ";";

	static final String PARAMETER_KEY_VALUE_CONNECTOR = "=";

	static final String[] QUOTEDS = new String[] { "\"", "'" };
	
	static final String MIME_TYPE_SPLIT = ",";

	/**
	 * Comparator used by {@link #sortBySpecificity(List)}.
	 */
	public static final Comparator<MimeType> SPECIFICITY_COMPARATOR = new SpecificityComparator<MimeType>();

	/**
	 * Public constant mime type that includes all media ranges (i.e.
	 * "&#42;/&#42;").
	 */
	public static final MimeType ALL = new SimpleMimeType("*", "*");

	/**
	 * A String equivalent of {@link MimeTypeUtils#ALL}.
	 */
	public static final String ALL_VALUE = "*/*";

	/**
	 * Public constant mime type for {@code application/json}.
	 */
	public static final MimeType APPLICATION_JSON = new SimpleMimeType("application", "json");

	/**
	 * A String equivalent of {@link MimeTypeConstants#APPLICATION_JSON}.
	 */
	public static final String APPLICATION_JSON_VALUE = "application/json";

	/**
	 * JSON规范类型为{@link MimeTypeConstants#APPLICATION_JSON}
	 */
	public static final MimeType TEXT_JSON = new SimpleMimeType("text", "json");
	
	/**
	 * JSON规范类型为{@link MimeTypeConstants#APPLICATION_JSON}
	 */
	public static final String TEXT_JSON_VALUE = "text/json";

	/**
	 * Public constant mime type for {@code application/octet-stream}.
	 */
	public static final MimeType APPLICATION_OCTET_STREAM = new SimpleMimeType("application", "octet-stream");

	/**
	 * A String equivalent of {@link MimeTypeUtils#APPLICATION_OCTET_STREAM}.
	 */
	public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

	/**
	 * Public constant mime type for {@code application/xml}.
	 */
	public static final MimeType APPLICATION_XML = new SimpleMimeType("application", "xml");

	/**
	 * A String equivalent of {@link MimeTypeUtils#APPLICATION_XML}.
	 */
	public static final String APPLICATION_XML_VALUE = "application/xml";

	/**
	 * Public constant mime type for {@code image/gif}.
	 */
	public static final MimeType IMAGE_GIF = new SimpleMimeType("image", "gif");

	/**
	 * A String equivalent of {@link MimeTypeUtils#IMAGE_GIF}.
	 */
	public static final String IMAGE_GIF_VALUE = "image/gif";

	/**
	 * Public constant mime type for {@code image/jpeg}.
	 */
	public static final MimeType IMAGE_JPEG = new SimpleMimeType("image", "jpeg");

	/**
	 * A String equivalent of {@link MimeTypeUtils#IMAGE_JPEG}.
	 */
	public static final String IMAGE_JPEG_VALUE = "image/jpeg";

	/**
	 * Public constant mime type for {@code image/png}.
	 */
	public static final MimeType IMAGE_PNG = new SimpleMimeType("image", "png");

	/**
	 * A String equivalent of {@link MimeTypeUtils#IMAGE_PNG}.
	 */
	public static final String IMAGE_PNG_VALUE = "image/png";

	/**
	 * Public constant mime type for {@code text/html}.
	 */
	public static final MimeType TEXT_HTML = new SimpleMimeType("text", "html");

	/**
	 * A String equivalent of {@link MimeTypeUtils#TEXT_HTML}.
	 */
	public static final String TEXT_HTML_VALUE = "text/html";

	/**
	 * Public constant mime type for {@code text/plain}.
	 */
	public static final MimeType TEXT_PLAIN = new SimpleMimeType("text", "plain");

	/**
	 * A String equivalent of {@link MimeTypeUtils#TEXT_PLAIN}.
	 */
	public static final String TEXT_PLAIN_VALUE = "text/plain";

	/**
	 * Public constant mime type for {@code text/xml}.
	 */
	public static final MimeType TEXT_XML = new SimpleMimeType("text", "xml");

	/**
	 * A String equivalent of {@link MimeTypeUtils#TEXT_XML}.
	 */
	public static final String TEXT_XML_VALUE = "text/xml";

	public static final MimeType APPLICATION_X_WWW_FORM_URLENCODED = new SimpleMimeType("application",
			"x-www-form-urlencoded");

	public static final String APPLICATION_X_WWW_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";

	public static final MimeType MULTIPART_FORM_DATA = new SimpleMimeType("multipart", "form-data");

	public static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";

	public static final MimeType TEXT_JAVASCRIPT = new SimpleMimeType("text", "javascript");

	public static final String TEXT_JAVASCRIPT_VALUE = "text/javascript";
}

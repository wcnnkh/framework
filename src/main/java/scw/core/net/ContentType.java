package scw.core.net;

import scw.core.KeyValuePair;

public interface ContentType {
	public static final String TEXT_HTML = "text/html";
	public static final String TEXT_PLAIN = "text/plain";
	public static final String TEXT_XML = "text/xml";
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";
	public static final String TEXT_JAVASCRIPT = "text/javascript";
	public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

	String getMimeType();

	String getCharsetName();

	KeyValuePair<String, String>[] getParams();

	String asString();
}

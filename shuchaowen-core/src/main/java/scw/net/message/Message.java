package scw.net.message;

import java.util.Enumeration;

import scw.net.MimeType;

public interface Message {
	String getHeader(String name);

	Enumeration<String> getHeaderNames();

	Enumeration<String> getHeaders(String name);

	Headers getHeaders();

	String getRawContentType();

	MimeType getContentType();

	long getContentLength();
}

package scw.net;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collection;

import scw.core.header.MultiValueHeadersReadOnly;

public interface Message extends MultiValueHeadersReadOnly {
	InputStream getInputStream();

	String getContentType();

	long getContentLength();

	String getContentEncoding();

	<T> T convert(Collection<MessageConverter> messageConverters, Type type) throws Throwable;

	byte[] toByteArray();

	String toString(String charsetName);
}

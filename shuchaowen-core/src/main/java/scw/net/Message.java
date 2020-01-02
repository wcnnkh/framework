package scw.net;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collection;

import scw.net.header.MultiValueHeadersReadOnly;
import scw.net.mime.MimeType;

public interface Message extends MultiValueHeadersReadOnly {
	InputStream getInputStream();

	MimeType getMimeType();

	long getContentLength();

	<T> T convert(Collection<MessageConverter> messageConverters, Type type) throws Throwable;

	byte[] toByteArray();

	String toString(String charsetName);
}

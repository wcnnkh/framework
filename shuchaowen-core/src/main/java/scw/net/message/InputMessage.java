package scw.net.message;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collection;

import scw.net.MimeType;
import scw.net.header.MultiValueHeadersReadOnly;
import scw.net.message.converter.MessageConverter;

public interface InputMessage extends MultiValueHeadersReadOnly {
	InputStream getInputStream();

	MimeType getMimeType();

	long getContentLength();

	<T> T convert(Collection<MessageConverter> messageConverters, Type type) throws Throwable;

	byte[] toByteArray();

	String toString(String charsetName);
}

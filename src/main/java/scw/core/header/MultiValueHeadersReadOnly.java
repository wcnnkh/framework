package scw.core.header;

import java.util.Enumeration;

public interface MultiValueHeadersReadOnly extends HeadersReadOnly {
	Enumeration<String> getHeaders(String name);
}

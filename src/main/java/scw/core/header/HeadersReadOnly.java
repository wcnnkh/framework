package scw.core.header;

import java.util.Enumeration;

public interface HeadersReadOnly extends HeadersConstants{
	String getHeader(String name);

	Enumeration<String> getHeaderNames();
}

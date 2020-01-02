package scw.net.header;

import java.util.Enumeration;

public interface HeadersReadOnly extends HeadersConstants{
	String getHeader(String name);

	Enumeration<String> getHeaderNames();
}

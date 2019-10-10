package scw.core.header;

public interface HeadersWriteOnly extends HeadersConstants{
	void setHeader(String name, String value);
}

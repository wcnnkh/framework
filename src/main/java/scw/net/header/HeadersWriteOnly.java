package scw.net.header;

public interface HeadersWriteOnly extends HeadersConstants{
	void setHeader(String name, String value);
}

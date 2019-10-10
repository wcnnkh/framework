package scw.core.header;

public interface MultiValueHeadersWriteOnly extends HeadersWriteOnly {
	void addHeader(String name, String value);
}

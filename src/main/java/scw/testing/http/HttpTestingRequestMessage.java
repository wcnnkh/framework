package scw.testing.http;

import java.util.Map;

import scw.core.io.ByteArray;

public interface HttpTestingRequestMessage {
	Map<String, String> getHeader();

	ByteArray getBody();

	String getMethod();

	String getPath();
}

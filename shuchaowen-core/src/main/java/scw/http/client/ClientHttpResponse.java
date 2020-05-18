package scw.http.client;

import java.io.Closeable;

public interface ClientHttpResponse extends ClientHttpInputMessage,
		Closeable {
	void close();
}

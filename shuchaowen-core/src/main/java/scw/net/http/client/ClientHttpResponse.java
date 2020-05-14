package scw.net.http.client;

import java.io.Closeable;

public interface ClientHttpResponse extends ClientHttpInputMessage,
		Closeable {
	void close();
}

package scw.net.http.server.mvc.output;

import java.io.IOException;

import scw.net.http.server.mvc.HttpChannel;

public interface Output {
	boolean canWrite(HttpChannel httpChannel, Object body);

	void write(HttpChannel httpChannel, Object body) throws IOException;
}

package io.basc.framework.net.server;

import java.io.IOException;

public interface Server {
	void service(ServerRequest request, ServerResponse response) throws IOException, ServerException;
}

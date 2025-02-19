package io.basc.framework.net.server;

import java.io.IOException;

public interface ServerFilter {
	void doFilter(ServerRequest request, ServerResponse response, Server chain) throws IOException, ServerException;
}

package io.basc.framework.net.server;

import java.io.IOException;

public interface Filter {
	void doFilter(ServerRequest request, ServerResponse response, Server chain) throws IOException, ServerException;
}

package run.soeasy.framework.net.server;

import java.io.IOException;

public interface ServerRequestDispatcher {
	void forward(ServerRequest request, ServerResponse response) throws IOException, ServerException;

	void include(ServerRequest request, ServerResponse response) throws IOException, ServerException;
}
